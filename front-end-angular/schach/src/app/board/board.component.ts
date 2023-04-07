import {Component, OnInit} from '@angular/core';
import {SchachService} from "../schach.service";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.css']
})
export class BoardComponent implements OnInit {
  initializationDone : boolean = false;

  constructor(public schach : SchachService, public route : ActivatedRoute) {
  }

  ngOnInit(): void {
    if (this.route.component?.name == "GameComponent" && this.initializationDone == false)
    {
      this.setBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBKQBNR");
      this.drawBoard();
      this.dragPiece();
      this.updateBoard();
      this.getFenString();
      this.setValidMoves();
      this.initializationDone = true;
    }
    else
    {
      this.setBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBKQBNR");
      this.drawBoard();
      this.updateBoard();
      this.getFenString();
      this.setValidMoves();
      this.initializationDone = false;
    }
  }

  SIZE : number = 500;
  FIELD : number = this.SIZE / 8;
  board : any[] = [];
  boardX : any[] = [];
  boardY : any[] = [];
  WIDTH : number = 60;
  offset : number = this.WIDTH / 2;
  isDrag : boolean = false;
  dragIndex : number = 0;
  validMoves : any[] = [0];

  resetBoard = () => {
    for (let i = 0; i < 64; i++) {
      this.board[i] = "";
    }
  }

  setBoard = (fenString : string) => {
    this.resetBoard();
    let idx = 0;
    let tokens = fenString.replace(/[/]/g, "").split("");

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
        ctx.drawImage(this.board[i], this.boardX[i], this.boardY[i], this.WIDTH, this.WIDTH)
      }
    }
  }

  updateBoard = () => {
    let canvas = <HTMLCanvasElement>document.getElementById("canvas");
    // @ts-ignore
    let ctx : CanvasRenderingContext2D = canvas.getContext("2d");
    let image = <HTMLImageElement>document.getElementById("k");
    canvas.width = 500;
    canvas.height = 500;
    for (let i = 0; i < 64; i++) {
      let x = i % 8 * this.FIELD;
      let y = Math.floor(i / 8) * this.FIELD;

      if (i % 2 + Math.floor(i / 8) % 2 == 1) {
        ctx.fillStyle = "#b58863";
      } else {
        ctx.fillStyle = "#f0d9b5";
      }

      /*if (i % 2 + Math.floor(i / 8) % 2 == 1) {
          ctx.fillStyle = "#825324";
      } else {
          ctx.fillStyle = "#e3c6aa";
      }*/
      ctx.fillRect(x, y, this.FIELD, this.FIELD)
    }
    for (let i = 0; i < 64; i++) {
      if (this.board[i] != '') {
        ctx.drawImage(this.board[i], this.boardX[i], this.boardY[i], 60, 60)
      }
    }
    if (this.isDrag) {
      this.drawValidFields();
      ctx.drawImage(this.board[this.dragIndex], this.boardX[this.dragIndex], this.boardY[this.dragIndex], 60, 60)
    }

    setTimeout(this.updateBoard, 1000 / 60)
  }

  isValidMove = (dragIndex : number, idx: number) => {


    return true;
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
          this.board[idx] = this.board[this.dragIndex];
          this.board[this.dragIndex] = '';
          this.setValidMoves();
        }
        this.boardX[idx] = idx % 8 * this.FIELD;
        this.boardY[idx] = Math.floor(idx / 8) * this.FIELD;
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
        if(num == 8){
          fen += num;
          num = 0;
        }else{
          num++;
        }
      }
      if((i+1)%8==0 && i < this.board.length-1){
        fen += (num != 0) ? num + "/" : "/";
        num = 0;
      }
    }
    if(num != 0){
      fen += num;
    }
    console.log(fen);
    return fen;
  }

  setValidMoves = () => {
    let init = {
      method: 'POST',
      body: this.getFenString()
    };

    fetch("http://localhost:8080/chess", init)
      .then(response => response.json())
      .then(result => this.validMoves = result)
      .catch(error => console.log('error', error));
  }


  drawValidFields = () => {
    let canvas = <HTMLCanvasElement>document.getElementById("canvas");
    let r = 15/2;
    // @ts-ignore
    let ctx : CanvasRenderingContext2D = canvas.getContext("2d");
    for (let m in this.validMoves) {
      //console.log(validMoves[m].startPos + " " + dragIndex)
      if(this.validMoves[m].startPos == this.dragIndex){
        ctx.beginPath();
        let idx = this.validMoves[m].targetPos;
        let x = idx % 8 * this.FIELD+(this.FIELD/2);
        let y = Math.floor(idx / 8) * this.FIELD+(this.FIELD/2);
        ctx.fillStyle = "#6e6e42";
        ctx.arc(x, y, r, 0, 2 * Math.PI)
        ctx.fill();
        //ctx.stroke()
        ctx.closePath();
      }
    }
  }
}
