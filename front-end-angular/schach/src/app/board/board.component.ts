import {Component, OnInit} from '@angular/core';
import {SchachService} from "../schach.service";
import {ActivatedRoute, NavigationEnd, Router} from "@angular/router";

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.css']
})
export class BoardComponent implements OnInit {
  constructor(public schach: SchachService, public route: ActivatedRoute, public router: Router) {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.router.navigate(['/']);
      }
    });
  }

  async ngOnInit(): Promise<void> {
    console.log("game");
    console.log(this.board)
    await this.setBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
    this.drawBoard();
    await this.setValidMoves(-1, -1);
    this.dragPiece();
    console.log(this.board);
    this.countdownLabel1 = document.getElementById("clockPlayer1") as HTMLLabelElement;
    this.countdownLabel2 = document.getElementById("clockPlayer2") as HTMLLabelElement;
    this.updateCountdownLabel();
    this.isPlayer = true;
    this.updateCountdownLabel();
    this.startCountdown();
  }

  SIZE: number = 500;
  FIELD: number = this.SIZE / 8;
  board: any[] = [];
  boardX: any[] = [];
  boardY: any[] = [];
  WIDTH: number = 60;
  offset: number = this.WIDTH / 2;
  isDrag: boolean = false;
  dragIndex: number = 0;
  validMoves: any = [0];
  color: boolean = true;
  singleMode: boolean = false;

  timeout: number = 0;
  lastStart: number = -1;
  lastTarget: number = -1;

  resetBoard = () => {
    for (let i = 0; i < 64; i++) {
      this.board[i] = "";
    }
  }

  setBoard = async (fenString : string) : Promise<void> => {
    await this.resetBoard();
    let idx = 0;
    let tokens = fenString.replace(/[/]/g, "").split("");
    clearTimeout(this.timeout);
    for (let i = 0; i < tokens.length; i++) {
      let char = tokens[i]
      let num = parseInt(char);
      if (isNaN(num)) {
        let image;
        if (char.charCodeAt(0) < 'a'.charCodeAt(0)) {
          image = <HTMLImageElement>document.getElementById(char);
        } else {
          image = <HTMLImageElement>document.getElementById('b' + char);
        }
        this.board[idx] = image;
        idx++;
      } else {
        idx += num;
      }
    }
    await this.updateBoard();
    if (this.isStarted)
    {
      this.stopCountdown();
      this.increaseCountdown();
      this.isPlayer = true;
      this.resumeCountdown();
    }
  }

  drawBoard = () => {
    console.log("draw")
    let canvas = <HTMLCanvasElement>document.getElementById("canvas");
    // @ts-ignore
    let ctx: CanvasRenderingContext2D = canvas.getContext("2d");
    let image = <HTMLImageElement>document.getElementById("k");
    canvas.width = 500;
    canvas.height = 500;
    for (let i = 0; i < 64; i++) {
      let x = i % 8 * this.FIELD;
      let y = Math.floor(i / 8) * this.FIELD;
      this.boardX[i] = x;
      this.boardY[i] = y;

      if (i % 2 + Math.floor(i / 8) % 2 == 1) {
        ctx.fillStyle = "#b58863";
      } else {
        ctx.fillStyle = "#f0d9b5";
      }
      ctx.fillRect(x, y, this.FIELD, this.FIELD)
      if (this.board[i] != '') {
        ctx.drawImage(this.board[i], this.boardX[i], this.boardY[i], this.WIDTH, this.WIDTH)
      }
    }
  }

  updateBoard = () => {
    let canvas = <HTMLCanvasElement>document.getElementById("canvas");
    // @ts-ignore
    let ctx: CanvasRenderingContext2D = canvas.getContext("2d");
    let image = <HTMLImageElement>document.getElementById("k");
    canvas.width = 500;
    canvas.height = 500;

    for (let i = 0; i < 64; i++) {
      let x = i % 8 * this.FIELD;
      let y = Math.floor(i / 8) * this.FIELD;
      if (i != this.dragIndex) {
        this.boardX[i] = x;
        this.boardY[i] = y;
      }
      /*if (i % 2 + Math.floor(i / 8) % 2 == 1) {
        ctx.fillStyle = "#b58863";
      } else {
        ctx.fillStyle = "#f0d9b5";
      }*/

      if (i % 2 + Math.floor(i / 8) % 2 == 1) {
        ctx.fillStyle = "#825324";
      } else {
        ctx.fillStyle = "#e3c6aa";
      }
      if (this.lastStart != -1 && this.lastTarget != -1 && this.lastTarget == i || this.lastStart == i) {
        ctx.fillStyle = "#cdd26a";
      }
      ctx.fillRect(x, y, this.FIELD, this.FIELD)
    }
    if(this.color){
      for (let i = 0; i < 64; i++) {
        if(this.board[i] != ''){
          ctx.drawImage(this.board[i], this.boardX[i], this.boardY[i], 60, 60)
        }
      }
    }else{
      let cnt :number = 0;
      for (let i = 63; i >= 0; i--) {
        if(this.board[i] != ''){
          ctx.drawImage(this.board[i], this.boardX[cnt], this.boardY[cnt], 60, 60)
        }
        cnt++;
      }
    }

    if(this.isDrag){
      this.drawValidFields();
      if(this.color){
        ctx.drawImage(this.board[this.dragIndex], this.boardX[this.dragIndex], this.boardY[this.dragIndex], 60, 60)
      }else{
        ctx.drawImage(this.board[63-this.dragIndex], this.boardX[this.dragIndex], this.boardY[this.dragIndex], 60, 60)
      }
    }
    this.timeout = setTimeout(this.updateBoard, 1000/60)
  }

  isValidMove = (dragIndex: number, idx: number) => {
    for (let m in this.validMoves) {
      if (this.validMoves[m].startPos == dragIndex && this.validMoves[m].targetPos == idx) {
        return true;
      }
    }

    return false;
  }

  dragPiece = () => {
    let canvas = <HTMLCanvasElement>document.getElementById("canvas");
    let ctx = canvas.getContext("2d");

    canvas.onmousedown = (evt) => {
      console.log("mouse Down")
      let rect = canvas.getBoundingClientRect();
      for (let i = 0; i < 64; i++) {
        let x = i % 8 * this.FIELD;
        let y = Math.floor(i / 8) * this.FIELD;
        let mx = evt.clientX - rect.x;
        let my = evt.clientY - rect.y;
        if ((mx > x && mx < x + this.FIELD) && (my > y && my < y + this.FIELD) && this.board[i] != '') {
          this.boardX[i] = mx - this.offset;
          this.boardY[i] = my - this.offset;
          this.isDrag = true;
          this.dragIndex = i;
        }
      }
    }

    canvas.onmousemove = (evt) => {
      let rect = canvas.getBoundingClientRect();
      if (this.isDrag) {
        this.boardX[this.dragIndex] = evt.clientX - rect.x - this.offset;
        this.boardY[this.dragIndex] = evt.clientY - rect.y - this.offset;
      }
    }

    canvas.onmouseup = (evt) => {
      if (this.isDrag) {
        let rect = canvas.getBoundingClientRect();
        this.isDrag = false;
        let mx = evt.clientX - rect.x;
        let my = evt.clientY - rect.y;
        let idx = this.getFieldIndex(mx, my);
        // GET

        if (idx != this.dragIndex && this.isValidMove(this.dragIndex, idx)) {
          this.lastTarget = idx;
          this.lastStart = this.dragIndex;
          this.board[idx] = this.board[this.dragIndex];
          this.board[this.dragIndex] = '';
          this.setValidMoves(this.dragIndex, idx);
        }
        this.boardX[this.dragIndex] = this.dragIndex % 8 * this.FIELD;
        this.boardY[this.dragIndex] = Math.floor(this.dragIndex / 8) * this.FIELD;
      }
    }

    canvas.onmouseout = () => {
      this.isDrag = false;
      this.boardX[this.dragIndex] = this.dragIndex % 8 * this.FIELD;
      this.boardY[this.dragIndex] = Math.floor(this.dragIndex / 8) * this.FIELD;
    }
  }

  getFieldIndex = (x : number, y : number) => {
    return Math.floor(x / this.FIELD) + Math.floor(y / this.FIELD) * 8;
  }

  getFenString = () => {
    let fen = "";
    let num = 0;
    let row = 0;
    let i = 0;

    for (let i = 0; i < this.board.length; i++) {
      if (this.board[i] != '') {
        let id = this.board[i].getAttribute("id");
        if (id.length == 2) {
          id = id.charAt(1);
        }
        fen += (num == 0) ? id : num + id;
        num = 0;
      } else {
        if (num == 8) {
          fen += num;
          num = 0;
        } else {
          num++;
        }
      }
      if ((i + 1) % 8 == 0 && i < this.board.length - 1) {
        fen += (num != 0) ? num + "/" : "/";
        num = 0;
      }
    }
    if (num != 0) {
      fen += num;
    }
    console.log(fen);
    return fen;
  }

  //change
  setValidMoves = (start : any, target : any) => {
    this.validMoves = [0];
    let init: object;
    let url: string;
    if (start == -1 && target == -1) {
      url = 'http://localhost:8080/chess/start';
      init = {
        method: 'POST',
        body: this.getFenString()
      };
      fetch(url, init)
        .then(response => response.json())
        .then(result => this.validMoves = result)
        .catch(error => console.log('error', error));
    } else {
      url = 'http://localhost:8080/chess/move';
      let move: object = {
        startPos: start,
        targetPos: target
      };
      init = {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(move)
      };
      fetch(url, init)
        .then(response => response.json())
        .then(json => {
          this.validMoves = json["moves"];
          this.lastStart = json["aiMove"].startPos;
          this.lastTarget = json["aiMove"].targetPos;
          this.setBoard(json["fenString"]);
        })
        .catch(error => console.log('error', error));
      this.stopCountdown();
      this.increaseCountdown();
      this.isPlayer = false;
      if (this.isStarted == false)
      {
        this.startCountdown();
        this.isStarted = true;
      }
      else
      {
        this.resumeCountdown();
      }
    }
  }

  drawValidFields = () => {
    let canvas = <HTMLCanvasElement>document.getElementById("canvas");
    let r = 15 / 2;
    // @ts-ignore
    let ctx: CanvasRenderingContext2D = canvas.getContext("2d");
    for (let m in this.validMoves) {
      //console.log(validMoves[m].startPos + " " + dragIndex)
      if (this.validMoves[m].startPos == this.dragIndex) {
        ctx.beginPath();
        let idx = this.validMoves[m].targetPos;
        let x = idx % 8 * this.FIELD + (this.FIELD / 2);
        let y = Math.floor(idx / 8) * this.FIELD + (this.FIELD / 2);
        if (this.board[idx] == '') {
          //ctx.fillStyle = "#6e6e42";
          ctx.fillStyle = "#00AA00";
        } else {
          ctx.fillStyle = "#AA0000";
        }

        ctx.arc(x, y, r, 0, 2 * Math.PI)
        ctx.fill();
        //ctx.stroke()
        ctx.closePath();
      }
    }
  }

  firstNumberOfPieces: number[] = [];

  drawDeadPieces = () => {
    let fen: string = this.getFenString();
    let numberOfPieces: number[] = [];
    let images: any = document.getElementsByClassName("piece");
    let blackPieces: string = "";
    let whitePieces: string = "";

    console.log(this.firstNumberOfPieces[0])

    if (this.firstNumberOfPieces[0] == undefined) {
      for (let i = 0; i < 12; i++) {
        let id: string = images[i].getAttribute("id");
        this.firstNumberOfPieces[i] = fen.split(id).length;
      }
    }
    for (let i = 0; i < 12; i++) {
      let id: string = images[i].getAttribute("id");
      for (let j = 0; j < this.firstNumberOfPieces[i] - fen.split(id).length; j++) {
        //console.log(images[i])
        if (id.length == 2) {
          blackPieces += images[i].outerHTML;
        } else {
          whitePieces += images[i].outerHTML;
        }
      }
    }
    console.log(blackPieces);
    console.log(whitePieces);
    console.log(document.getElementsByTagName("div"));

    // @ts-ignore
    (document.getElementsByClassName("deadPiecesBlack"))[0].innerHTML = blackPieces;
    // @ts-ignore
    (document.getElementsByClassName("deadPiecesWhite"))[0].innerHTML = whitePieces;
  }

  // @ts-ignore
  countdownLabel1: HTMLLabelElement;
  countdownTime1: number = 10 * 60;
  countdownInterval1: any;
  isCountingDown1: boolean = false;
  // @ts-ignore
  countdownLabel2: HTMLLabelElement;
  countdownTime2: number = 10 * 60;
  countdownInterval2: any;
  isCountingDown2: boolean = false;
  isPlayer : boolean = false;
  isStarted : boolean = false;

  startCountdown() {
    if (this.isPlayer)
    {
      this.isCountingDown2 = true;

      this.countdownInterval2 = setInterval(() => {
        this.countdownTime2--;
        this.updateCountdownLabel();

        if (this.countdownTime2 <= 0) {
          this.stopCountdown();
          alert("TIME OVER !!!");
          this.schach.router.navigate(['/']);
        }
      }, 1000);
    }
    else
    {
      this.isCountingDown1 = true;

      this.countdownInterval1 = setInterval(() => {
        this.countdownTime1--;
        this.updateCountdownLabel();

        if (this.countdownTime1 <= 0) {
          this.stopCountdown();
          alert("TIME OVER !!!");
          this.schach.router.navigate(['/']);
        }
      }, 1000);
    }
  }

  stopCountdown() {
    if (this.isPlayer)
    {
      clearInterval(this.countdownInterval2);
      this.isCountingDown2 = false;
    }
    else
    {
      clearInterval(this.countdownInterval1);
      this.isCountingDown1 = false;
    }
  }

  resumeCountdown() {
    if (this.isPlayer)
    {
      if (!this.isCountingDown2 && this.countdownTime2 > 0) {
        this.startCountdown();
      }
    }
    else
    {
      if (!this.isCountingDown1 && this.countdownTime1 > 0) {
        this.startCountdown();
      }
    }
  }

  increaseCountdown() {
    if (this.isPlayer)
    {
      this.countdownTime2 += 10;
      this.updateCountdownLabel();
    }
    else
    {
      this.countdownTime1 += 10;
      this.updateCountdownLabel();
    }
  }

  updateCountdownLabel() {
    if (this.isPlayer)
    {
      const minutes = Math.floor(this.countdownTime2 / 60);
      const seconds = this.countdownTime2 % 60;

      const formattedMinutes = String(minutes).padStart(2, "0");
      const formattedSeconds = String(seconds).padStart(2, "0");

      this.countdownLabel2.innerText = `🕒 ${formattedMinutes}:${formattedSeconds}`;
    }
    else
    {
      const minutes = Math.floor(this.countdownTime1 / 60);
      const seconds = this.countdownTime1 % 60;

      const formattedMinutes = String(minutes).padStart(2, "0");
      const formattedSeconds = String(seconds).padStart(2, "0");

      this.countdownLabel1.innerText = `🕒 ${formattedMinutes}:${formattedSeconds}`;
    }
  }

  ngOnDestroy(): void {
    this.stopCountdown();
  }
}
