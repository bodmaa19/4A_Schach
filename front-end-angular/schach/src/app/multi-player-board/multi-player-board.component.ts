import {Component, OnInit} from '@angular/core';
import {SchachService} from "../schach.service";
import {ActivatedRoute, NavigationEnd, Router} from "@angular/router";

@Component({
  selector: 'app-multi-player-board',
  templateUrl: './multi-player-board.component.html',
  styleUrls: ['./multi-player-board.component.css']
})
export class MultiPlayerBoardComponent implements OnInit {
  constructor(public schach: SchachService, public route: ActivatedRoute, public router: Router) {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.router.navigate(['/']);
      }
    });
  }

  async ngOnInit(): Promise<void> {
    this.setBoard(this.lastFen);
    this.loginPlayer();
    setTimeout(this.startMulti, 1000);
    this.countdownLabel1 = document.getElementById("clockPlayer1") as HTMLLabelElement;
    this.countdownLabel2 = document.getElementById("clockPlayer2") as HTMLLabelElement;
    this.updateCountdownLabel();
    this.isPlayer = true;
    this.updateCountdownLabel();
    this.startCountdown();
    (<HTMLDivElement>document.getElementById("wait")).style.display = "block";
    (<HTMLDivElement>document.getElementById("chess")).style.display = "none";
  }

  SIZE = 500;
  FIELD = this.SIZE / 8;
  board : any = [];
  boardX : any = [];
  boardY : any = [];
  WIDTH = 60;
  offset = this.WIDTH / 2;
  isDrag = false;
  dragIndex : any;
  validMoves : any;

  timeout : any;
  lastStart = -1;
  lastTarget = -1;
  isWhite = true;
  multiColor = false;
  lastFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
  isSingle = true;
  turn : any;

  startMulti = () => {
    this.isSingle = false;
    this.drawBoard();
    this.dragPiece();
    this.updateBoard();
    this.touchPiece();
    let url = 'http://localhost:8080/chess/angular/start?isSinglePlayer=false' + "&playerId="+this.player["playerId"];

    fetch(url)
      .then(response => {
        console.log(response.json())
        return response.json()
      })
      .then(result => this.validMoves = result)
      .catch(error => console.log('error', error));
    this.waitForPlayer();
  }

  waitTimeout = 0;
  waitForPlayer = async () => {
    let url = 'http://localhost:8080/chess/angular/wait?'+ "&playerId="+this.player["playerId"];

    let check = false;
    await fetch(url)
      .then(response => {
        return response.json()
      })
      .then(result => {
        check = true;
        this.schach.setOpponentPlayer(
          {
            username: result["opponent"].username,
            best_score: 10000
          }
        );
        this.validMoves = result["moves"];
        this.multiColor = result["white"];
        this.turn = result["whiteTurn"];
        if (!this.multiColor) {
          this.changeColor()
        }
        this.updateMulti();
      })
      .catch(error => check = false);
    if (check) {
      (<HTMLDivElement>document.getElementById("wait")).style.display = "none";
      (<HTMLDivElement>document.getElementById("chess")).style.display = "block";
      clearTimeout(this.waitTimeout);
    } else {
      this.waitTimeout = window.setTimeout(this.waitForPlayer, 1000);
    }
  }

  updateMulti = async () => {
    if (this.multiColor != this.turn) {
      let url = 'http://localhost:8080/chess/angular/multi/checkBoard?turn=' + this.turn + "&playerId="+this.player["playerId"];

      await fetch(url)
        .then(response => {
          return response.json()
        })
        .then(result => {
          console.log(result)
          this.validMoves = result["moves"];
          this.multiColor = result["white"];
          this.turn = result["whiteTurn"];
          this.lastStart = result["aiMove"].startPos;
          this.lastTarget = result["aiMove"].targetPos;
          this.setBoard(result["fenString"]);
        })
        .catch(error => console.log(error));
    }
    window.setTimeout(this.updateMulti, 1000);
  }

  resetBoard = () => {
    for (let i = 0; i < 64; i++) {
      this.board[i] = "";
    }
  }

  // @ts-ignore
  setBoard = async (fenString) => {
    await this.resetBoard();
    let idx = 0;
    let tokens = fenString.replace(/[/]/g, "").split("");
    clearTimeout(this.timeout);
    if (this.isWhite) {
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
    } else {
      for (let i = tokens.length - 1; i >= 0; i--) {
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
    }
    this.updateBoard();
    this.updateBoard();
    if (this.isStarted)
    {
      this.stopCountdown();
      this.increaseCountdown();
      this.isPlayer = true;
      this.resumeCountdown();
    }
  }

  drawBoard = () => {
    let canvas = <HTMLCanvasElement>document.getElementById("canvas");
    // @ts-ignore
    let ctx : CanvasRenderingContext2D = canvas.getContext("2d");
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
        ctx.save();
        ctx.rotate(180 * Math.PI / 180);
        ctx.drawImage(this.board[i], this.boardX[i], this.boardY[i], this.WIDTH, this.WIDTH)
        ctx.restore();
      }
    }
  }

  updateBoard = () => {
    let canvas = <HTMLCanvasElement>document.getElementById("canvas");
    canvas.style.rotate = (this.isWhite) ? "0deg" : "180deg";
    // @ts-ignore
    let ctx : CanvasRenderingContext2D = canvas.getContext("2d");
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
        ctx.fillStyle = "#00DD00";
      }
      ctx.fillRect(x, y, this.FIELD, this.FIELD)
    }
    for (let i = 0; i < 64; i++) {
      if (this.board[i] != '') {
        ctx.save();
        if (!this.isWhite) {
          ctx.translate(this.FIELD * 8, this.FIELD * 8);
          ctx.rotate(180 * Math.PI / 180);
        }
        ctx.drawImage(this.board[i], this.boardX[i], this.boardY[i], 60, 60)
        ctx.restore();
      }
    }
    if (this.isDrag) {
      this.drawValidFields();
      ctx.save();
      if (!this.isWhite) {
        ctx.translate(this.FIELD * 8, this.FIELD * 8);
        ctx.rotate(180 * Math.PI / 180);
      }
      ctx.drawImage(this.board[this.dragIndex], this.boardX[this.dragIndex], this.boardY[this.dragIndex], 60, 60)
      ctx.restore();
    }

    this.timeout = setTimeout(this.updateBoard, 1000 / 60)
  }

  isValidMove = (dragIndex: number, idx: number) => {
    if (this.isWhite) {
      for (let m in this.validMoves) {
        if (this.validMoves[m].startPos == dragIndex && this.validMoves[m].targetPos == idx) {
          return true;
        }
      }
    } else {
      for (let m in this.validMoves) {
        if (63 - this.validMoves[m].startPos == dragIndex && 63 - this.validMoves[m].targetPos == idx) {
          return true;
        }
      }
    }

    return false;
  }

  dragPiece = () => {
    let canvas = <HTMLCanvasElement>document.getElementById("canvas");
    let ctx = canvas.getContext("2d");

    canvas.onmousedown = (evt) => {
      let rect = canvas.getBoundingClientRect();
      for (let i = 0; i < 64; i++) {
        let x = i % 8 * this.FIELD;
        let y = Math.floor(i / 8) * this.FIELD;
        let mx = evt.clientX - rect.x;
        let my = evt.clientY - rect.y;
        if ((mx > x && mx < x + this.FIELD) && (my > y && my < y + this.FIELD) && this.board[i] != '') {
          if ((this.multiColor && this.board[i].getAttribute("id").length == 1)
            || (!this.multiColor && this.board[i].getAttribute("id").length == 2) && this.multiColor == this.isWhite) {
            this.boardX[i] = mx - this.offset;
            this.boardY[i] = my - this.offset;
            this.isDrag = true;
            this.dragIndex = i;
          }
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
          this.makeMove(this.dragIndex, idx);
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

  touchPiece = () => {
    let canvas = <HTMLCanvasElement>document.getElementById("canvas");
    let ctx = canvas.getContext("2d");

    canvas.ontouchstart = (evt) => {
      let rect = canvas.getBoundingClientRect();
      for (let i = 0; i < 64; i++) {
        let x = i % 8 * this.FIELD;
        let y = Math.floor(i / 8) * this.FIELD;
        let mx = evt.touches[0].clientX - rect.x;
        let my = evt.touches[0].clientY - rect.y;
        if ((mx > x && mx < x + this.FIELD) && (my > y && my < y + this.FIELD) && this.board[i] != '') {
          if ((this.multiColor && this.board[i].getAttribute("id").length == 1)
            || (!this.multiColor && this.board[i].getAttribute("id").length == 2) && this.multiColor == this.isWhite) {
            this.boardX[i] = mx - this.offset;
            this.boardY[i] = my - this.offset;
            this.isDrag = true;
            this.dragIndex = i;
          }
        }
      }
    }

    canvas.ontouchmove = (evt) => {
      let rect = canvas.getBoundingClientRect();
      if (this.isDrag) {
        this.boardX[this.dragIndex] = evt.touches[0].clientX - rect.x - this.offset;
        this.boardY[this.dragIndex] = evt.touches[0].clientY - rect.y - this.offset;
      }
    }

    canvas.ontouchend = (evt) => {

      if (this.isDrag) {
        let rect = canvas.getBoundingClientRect();
        this.isDrag = false;
        let mx = evt.touches[0].clientX - rect.x;
        let my = evt.touches[0].clientY - rect.y;
        let idx = this.getFieldIndex(this.boardX[this.dragIndex], this.boardY[this.dragIndex]);
        // GET

        if (idx != this.dragIndex && this.isValidMove(this.dragIndex, idx)) {
          this.lastTarget = idx;
          this.lastStart = this.dragIndex;
          this.board[idx] = this.board[this.dragIndex];
          this.board[this.dragIndex] = '';
          if (this.isSingle) {
            this.makeMove(this.dragIndex, idx);
          }
        }
        this.boardX[this.dragIndex] = this.dragIndex % 8 * this.FIELD;
        this.boardY[this.dragIndex] = Math.floor(this.dragIndex / 8) * this.FIELD;
      }
    }

    canvas.ontouchcancel = () => {
      this.isDrag = false;
      this.boardX[this.dragIndex] = this.dragIndex % 8 * this.FIELD;
      this.boardY[this.dragIndex] = Math.floor(this.dragIndex / 8) * this.FIELD;
    }
  }


  getFieldIndex = (x:any, y:any) => {
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
  makeMove = (start : any, target : any) => {
    this.validMoves = 0;
    let url: string;
    let startPos = (this.isWhite) ? start : 63 - start
    let targetPos = (this.isWhite) ? target : 63 - target

    if (this.isSingle) {
      url = 'http://localhost:8080/chess/move/angular/single?startPos=' + startPos + '&targetPos=' + targetPos + "&playerId="+this.player["playerId"];
    } else {
      url = 'http://localhost:8080/chess/angular/move/multi?startPos=' + startPos + '&targetPos=' + targetPos + "&playerId="+this.player["playerId"];
    }

    fetch(url)
      .then(response => {
        return response.json()
      })
      .then(json => {
        console.log(json["fenString"])
        this.validMoves = json["moves"];
        this.lastStart = json["aiMove"].startPos;
        this.lastTarget = json["aiMove"].targetPos;
        this.turn = json["whiteTurn"];
        this.lastFen = json["fenString"];
        console.log(this.lastFen)
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

  drawValidFields = () => {
    let canvas = <HTMLCanvasElement>document.getElementById("canvas");
    let r = 15 / 2;
    // @ts-ignore
    let ctx : CanvasRenderingContext2D = canvas.getContext("2d");
    for (let m in this.validMoves) {
      //console.log(validMoves[m].startPos + " " + dragIndex)
      let clickedIdx = (this.isWhite) ? this.dragIndex : 63 - this.dragIndex;
      if (this.validMoves[m].startPos == clickedIdx) {
        ctx.beginPath();
        let idx = this.validMoves[m].targetPos;
        let x = idx % 8 * this.FIELD + (this.FIELD / 2);
        let y = Math.floor(idx / 8) * this.FIELD + (this.FIELD / 2);
        if (this.board[(this.isWhite) ? idx : 63 - idx] == '') {
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

  changeColor = async () => {
    this.isWhite = !this.isWhite;
    console.log(this.isWhite)
    console.log(this.lastFen)
    this.setBoard(this.lastFen);
    /*for(let key in validMoves){
        validMoves[key].startPos = 63 - validMoves[key].startPos;
        validMoves[key].targetPos = 63 - validMoves[key].targetPos;
        console.log(key);
    }*/
  }

  player : any;
  loginPlayer = () => {
    let url = "http://localhost:8080/chess/angular/login";
    let init = {
      method: 'POST',
      body: this.schach.player["userId"]
    };
    console.log(init);
    fetch(url, init)
      .then(response => {
        return response.json()
      })
      .then(json => {
        this.player = json;
        console.log(json)
        let text = "Hallo " + this.player["name"] + "! (id: " + this.player["playerId"] + ")"
      })
      .catch(error => console.log('error', error));
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
          blackPieces += `<img style="height: 80px; width: auto" src="assets/images/${images[i].getAttribute("id").toUpperCase()}.png"></img>`;
        } else {
          whitePieces += `<img style="height: 80px; width: auto" src="assets/images/W${images[i].getAttribute("id")}.png"></img>`;
        }
      }
    }
    /*console.log(blackPieces);
    console.log(whitePieces);
    console.log(document.getElementsByTagName("div"));*/

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
