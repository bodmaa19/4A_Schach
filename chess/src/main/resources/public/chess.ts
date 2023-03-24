const SIZE = 500;
const FIELD = SIZE / 8;
let board = [];
let boardX = [];
let boardY = [];
const WIDTH = 60;
const offset = WIDTH / 2;
let isDrag = false;
let dragIndex;
let validMoves;

window.addEventListener("load", () => {
    setBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBKQBNR");
    //setBoard("8/8/8/2k5/4K3/8/8/8")
    drawBoard();
    dragPiece();
    updateBoard();
    getFenString();
    setValidMoves();
})

const resetBoard = () => {
    for (let i = 0; i < 64; i++) {
        board[i] = "";
    }
}

const setBoard = (fenString) => {
    resetBoard();
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
            board[idx] = image;
            idx++;
        } else {
            idx += num;
        }
    }
}

const drawBoard = () => {
    let canvas = <HTMLCanvasElement>document.getElementById("canvas");
    let ctx = canvas.getContext("2d");
    let image = <HTMLImageElement>document.getElementById("k");
    canvas.width = 500;
    canvas.height = 500;
    for (let i = 0; i < 64; i++) {
        let x = i % 8 * FIELD;
        let y = Math.floor(i / 8) * FIELD;
        boardX[i] = x;
        boardY[i] = y;

        if (i % 2 + Math.floor(i / 8) % 2 == 1) {
            ctx.fillStyle = "#b58863";
        } else {
            ctx.fillStyle = "#f0d9b5";
        }
        ctx.fillRect(x, y, FIELD, FIELD)
        if (board[i] != '') {
            ctx.drawImage(board[i], boardX[i], boardY[i], WIDTH, WIDTH)
        }
    }
}

const updateBoard = () => {
    let canvas = <HTMLCanvasElement>document.getElementById("canvas");
    let ctx = canvas.getContext("2d");
    let image = <HTMLImageElement>document.getElementById("k");
    canvas.width = 500;
    canvas.height = 500;
    for (let i = 0; i < 64; i++) {
        let x = i % 8 * FIELD;
        let y = Math.floor(i / 8) * FIELD;

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
        ctx.fillRect(x, y, FIELD, FIELD)
    }
    for (let i = 0; i < 64; i++) {
        if (board[i] != '') {
            ctx.drawImage(board[i], boardX[i], boardY[i], 60, 60)
        }
    }
    if (isDrag) {
        drawValidFields();
        ctx.drawImage(board[dragIndex], boardX[dragIndex], boardY[dragIndex], 60, 60)
    }

    setTimeout(updateBoard, 1000 / 60)
}

const isValidMove = (dragIndex, idx: number) => {


    return true;
}

const dragPiece = () => {
    let canvas = <HTMLCanvasElement>document.getElementById("canvas");
    let ctx = canvas.getContext("2d");

    canvas.onmousedown = (evt) => {
        let rect = canvas.getBoundingClientRect();
        for (let i = 0; i < 64; i++) {
            let x = i % 8 * FIELD;
            let y = Math.floor(i / 8) * FIELD;
            let mx = evt.clientX - rect.x;
            let my = evt.clientY - rect.y;
            if ((mx > x && mx < x + FIELD) && (my > y && my < y + FIELD) && board[i] != '') {
                boardX[i] = mx - offset;
                boardY[i] = my - offset;
                isDrag = true;
                dragIndex = i;
            }
        }
    }

    canvas.onmousemove = (evt) => {
        let rect = canvas.getBoundingClientRect();
        if (isDrag) {
            boardX[dragIndex] = evt.clientX - rect.x - offset;
            boardY[dragIndex] = evt.clientY - rect.y - offset;
        }
    }

    canvas.onmouseup = (evt) => {
        if (isDrag) {
            let rect = canvas.getBoundingClientRect();
            isDrag = false;
            let mx = evt.clientX - rect.x;
            let my = evt.clientY - rect.y;
            let idx = getFieldIndex(mx, my);
            // GET

            if (idx != dragIndex && isValidMove(dragIndex, idx)) {
                board[idx] = board[dragIndex];
                board[dragIndex] = '';
                setValidMoves();
            }
            boardX[idx] = idx % 8 * FIELD;
            boardY[idx] = Math.floor(idx / 8) * FIELD;
        }
    }

    canvas.onmouseout = () => {
        isDrag = false;
        boardX[dragIndex] = dragIndex % 8 * FIELD;
        boardY[dragIndex] = Math.floor(dragIndex / 8) * FIELD;
    }
}

const getFieldIndex = (x, y) => {
    return Math.floor(x / FIELD) + Math.floor(y / FIELD) * 8;
}

const getFenString = () => {
    let fen = "";
    let num = 0;
    let row = 0;
    let i = 0;

    for (let i = 0; i < board.length; i++) {
        if (board[i] != '') {
            let id = board[i].getAttribute("id");
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
        if((i+1)%8==0 && i < board.length-1){
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

const setValidMoves = () => {
    let init = {
        method: 'POST',
        body: getFenString()
    };

    fetch("http://localhost:8080/chess", init)
        .then(response => response.json())
        .then(result => validMoves = result)
        .catch(error => console.log('error', error));
}


const drawValidFields = () => {
    let canvas = <HTMLCanvasElement>document.getElementById("canvas");
    let r = 15/2;
    let ctx = canvas.getContext("2d");
    for (let m in validMoves) {
        //console.log(validMoves[m].startPos + " " + dragIndex)
        if(validMoves[m].startPos == dragIndex){
            ctx.beginPath();
            let idx = validMoves[m].targetPos;
            let x = idx % 8 * FIELD+(FIELD/2);
            let y = Math.floor(idx / 8) * FIELD+(FIELD/2);
            ctx.fillStyle = "#6e6e42";
            ctx.arc(x, y, r, 0, 2 * Math.PI)
            ctx.fill();
            //ctx.stroke()
            ctx.closePath();
        }
    }
}