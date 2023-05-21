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
let timeout;
let lastStart = -1;
let lastTarget = -1;
let isWhite = true;
let multiColor = false;
let lastFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
let isSingle = true;
let turn;
window.addEventListener("load", () => {
    setBoard(lastFen);
    loginPlayer();
    //setBoard("r1bqkb1r/ppp2ppp/2n2n2/3pp3/3PP3/2N2N2/PPP2PPP/R1BQKB1R")
    //setBoard("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R")
    //setBoard("r2k3r/8/8/8/8/8/8/4K3")
});
const startSingle = () => {
    isSingle = true;
    isWhite = true;
    multiColor = true;
    drawBoard();
    dragPiece();
    updateBoard();
    touchPiece();
    let url = './chess/start?isSinglePlayer=true';
    let init = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(player)
    };
    fetch(url, init)
        .then(response => response.json())
        .then(result => validMoves = result)
        .catch(error => console.log('error', error));
    document.getElementById("match").style.display = "none";
    document.getElementById("chess").style.display = "block";
};
const startMulti = () => {
    isSingle = false;
    drawBoard();
    dragPiece();
    updateBoard();
    touchPiece();
    let url = './chess/start?isSinglePlayer=false';
    let init = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(player)
    };
    fetch(url, init)
        .then(response => response.json())
        .then(result => validMoves = result)
        .catch(error => console.log('error', error));
    document.getElementById("match").style.display = "none";
    document.getElementById("wait").style.display = "block";
    waitForPlayer();
};
let waitTimeout = 0;
const waitForPlayer = async () => {
    let url = './chess/wait';
    let init = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(player)
    };
    let check = false;
    await fetch(url, init)
        .then(response => response.json())
        .then(result => {
        check = true;
        console.log(result);
        validMoves = result["moves"];
        multiColor = result["white"];
        turn = result["whiteTurn"];
        if (!multiColor) {
            changeColor();
        }
        updateMulti();
    })
        .catch(error => check = false);
    if (check) {
        document.getElementById("wait").style.display = "none";
        document.getElementById("chess").style.display = "block";
        clearTimeout(waitTimeout);
    }
    else {
        waitTimeout = window.setTimeout(waitForPlayer, 1000);
    }
};
const updateMulti = async () => {
    if (multiColor != turn) {
        let url = './chess/multi/checkBoard?turn=' + turn;
        let init = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(player)
        };
        await fetch(url, init)
            .then(response => response.json())
            .then(result => {
            console.log(result);
            validMoves = result["moves"];
            multiColor = result["white"];
            turn = result["whiteTurn"];
            lastStart = result["aiMove"].startPos;
            lastTarget = result["aiMove"].targetPos;
            setBoard(result["fenString"]);
        })
            .catch(error => console.log(error));
    }
    window.setTimeout(updateMulti, 1000);
};
const resetBoard = () => {
    for (let i = 0; i < 64; i++) {
        board[i] = "";
    }
};
// @ts-ignore
const setBoard = async (fenString) => {
    await resetBoard();
    let idx = 0;
    let tokens = fenString.replace(/[/]/g, "").split("");
    clearTimeout(timeout);
    if (isWhite) {
        for (let i = 0; i < tokens.length; i++) {
            let char = tokens[i];
            let num = parseInt(char);
            if (isNaN(num)) {
                let image;
                if (char.charCodeAt(0) < 'a'.charCodeAt(0)) {
                    image = document.getElementById(char);
                }
                else {
                    image = document.getElementById('b' + char);
                }
                board[idx] = image;
                idx++;
            }
            else {
                idx += num;
            }
        }
    }
    else {
        for (let i = tokens.length - 1; i >= 0; i--) {
            let char = tokens[i];
            let num = parseInt(char);
            if (isNaN(num)) {
                let image;
                if (char.charCodeAt(0) < 'a'.charCodeAt(0)) {
                    image = document.getElementById(char);
                }
                else {
                    image = document.getElementById('b' + char);
                }
                board[idx] = image;
                idx++;
            }
            else {
                idx += num;
            }
        }
    }
    updateBoard();
};
const drawBoard = () => {
    let canvas = document.getElementById("canvas");
    let ctx = canvas.getContext("2d");
    let image = document.getElementById("k");
    canvas.width = 500;
    canvas.height = 500;
    for (let i = 0; i < 64; i++) {
        let x = i % 8 * FIELD;
        let y = Math.floor(i / 8) * FIELD;
        boardX[i] = x;
        boardY[i] = y;
        if (i % 2 + Math.floor(i / 8) % 2 == 1) {
            ctx.fillStyle = "#b58863";
        }
        else {
            ctx.fillStyle = "#f0d9b5";
        }
        ctx.fillRect(x, y, FIELD, FIELD);
        if (board[i] != '') {
            ctx.save();
            ctx.rotate(180 * Math.PI / 180);
            ctx.drawImage(board[i], boardX[i], boardY[i], WIDTH, WIDTH);
            ctx.restore();
        }
    }
};
const updateBoard = () => {
    let canvas = document.getElementById("canvas");
    canvas.style.rotate = (isWhite) ? "0deg" : "180deg";
    let ctx = canvas.getContext("2d");
    let image = document.getElementById("k");
    canvas.width = 500;
    canvas.height = 500;
    for (let i = 0; i < 64; i++) {
        let x = i % 8 * FIELD;
        let y = Math.floor(i / 8) * FIELD;
        if (i != dragIndex) {
            boardX[i] = x;
            boardY[i] = y;
        }
        if (i % 2 + Math.floor(i / 8) % 2 == 1) {
            ctx.fillStyle = "#b58863";
        }
        else {
            ctx.fillStyle = "#f0d9b5";
        }
        if (lastStart != -1 && lastTarget != -1 && lastTarget == i || lastStart == i) {
            ctx.fillStyle = "#cdd26a";
        }
        /*if (i % 2 + Math.floor(i / 8) % 2 == 1) {
            ctx.fillStyle = "#825324";
        } else {
            ctx.fillStyle = "#e3c6aa";
        }*/
        ctx.fillRect(x, y, FIELD, FIELD);
    }
    for (let i = 0; i < 64; i++) {
        if (board[i] != '') {
            ctx.save();
            if (!isWhite) {
                ctx.translate(FIELD * 8, FIELD * 8);
                ctx.rotate(180 * Math.PI / 180);
            }
            ctx.drawImage(board[i], boardX[i], boardY[i], 60, 60);
            ctx.restore();
        }
    }
    if (isDrag) {
        drawValidFields();
        ctx.save();
        if (!isWhite) {
            ctx.translate(FIELD * 8, FIELD * 8);
            ctx.rotate(180 * Math.PI / 180);
        }
        ctx.drawImage(board[dragIndex], boardX[dragIndex], boardY[dragIndex], 60, 60);
        ctx.restore();
    }
    timeout = setTimeout(updateBoard, 1000 / 60);
};
const isValidMove = (dragIndex, idx) => {
    if (isWhite) {
        for (let m in validMoves) {
            if (validMoves[m].startPos == dragIndex && validMoves[m].targetPos == idx) {
                return true;
            }
        }
    }
    else {
        for (let m in validMoves) {
            if (63 - validMoves[m].startPos == dragIndex && 63 - validMoves[m].targetPos == idx) {
                return true;
            }
        }
    }
    return false;
};
const dragPiece = () => {
    let canvas = document.getElementById("canvas");
    let ctx = canvas.getContext("2d");
    canvas.onmousedown = (evt) => {
        let rect = canvas.getBoundingClientRect();
        for (let i = 0; i < 64; i++) {
            let x = i % 8 * FIELD;
            let y = Math.floor(i / 8) * FIELD;
            let mx = evt.clientX - rect.x;
            let my = evt.clientY - rect.y;
            if ((mx > x && mx < x + FIELD) && (my > y && my < y + FIELD) && board[i] != '') {
                if ((multiColor && board[i].getAttribute("id").length == 1)
                    || (!multiColor && board[i].getAttribute("id").length == 2) && multiColor == isWhite) {
                    boardX[i] = mx - offset;
                    boardY[i] = my - offset;
                    isDrag = true;
                    dragIndex = i;
                }
            }
        }
    };
    canvas.onmousemove = (evt) => {
        let rect = canvas.getBoundingClientRect();
        if (isDrag) {
            boardX[dragIndex] = evt.clientX - rect.x - offset;
            boardY[dragIndex] = evt.clientY - rect.y - offset;
        }
    };
    canvas.onmouseup = (evt) => {
        if (isDrag) {
            let rect = canvas.getBoundingClientRect();
            isDrag = false;
            let mx = evt.clientX - rect.x;
            let my = evt.clientY - rect.y;
            let idx = getFieldIndex(mx, my);
            // GET
            if (idx != dragIndex && isValidMove(dragIndex, idx)) {
                lastTarget = idx;
                lastStart = dragIndex;
                board[idx] = board[dragIndex];
                board[dragIndex] = '';
                makeMoveSingle(dragIndex, idx);
            }
            boardX[dragIndex] = dragIndex % 8 * FIELD;
            boardY[dragIndex] = Math.floor(dragIndex / 8) * FIELD;
        }
    };
    canvas.onmouseout = () => {
        isDrag = false;
        boardX[dragIndex] = dragIndex % 8 * FIELD;
        boardY[dragIndex] = Math.floor(dragIndex / 8) * FIELD;
    };
};
const touchPiece = () => {
    let canvas = document.getElementById("canvas");
    let ctx = canvas.getContext("2d");
    canvas.ontouchstart = (evt) => {
        let rect = canvas.getBoundingClientRect();
        for (let i = 0; i < 64; i++) {
            let x = i % 8 * FIELD;
            let y = Math.floor(i / 8) * FIELD;
            let mx = evt.touches[0].clientX - rect.x;
            let my = evt.touches[0].clientY - rect.y;
            if ((mx > x && mx < x + FIELD) && (my > y && my < y + FIELD) && board[i] != '') {
                if ((multiColor && board[i].getAttribute("id").length == 1)
                    || (!multiColor && board[i].getAttribute("id").length == 2) && multiColor == isWhite) {
                    boardX[i] = mx - offset;
                    boardY[i] = my - offset;
                    isDrag = true;
                    dragIndex = i;
                }
            }
        }
    };
    canvas.ontouchmove = (evt) => {
        document.getElementById("info").innerText = "end";
        let rect = canvas.getBoundingClientRect();
        if (isDrag) {
            boardX[dragIndex] = evt.touches[0].clientX - rect.x - offset;
            boardY[dragIndex] = evt.touches[0].clientY - rect.y - offset;
        }
    };
    canvas.ontouchend = (evt) => {
        if (isDrag) {
            let rect = canvas.getBoundingClientRect();
            isDrag = false;
            let mx = evt.touches[0].clientX - rect.x;
            let my = evt.touches[0].clientY - rect.y;
            let idx = getFieldIndex(boardX[dragIndex], boardY[dragIndex]);
            // GET
            if (idx != dragIndex && isValidMove(dragIndex, idx)) {
                lastTarget = idx;
                lastStart = dragIndex;
                board[idx] = board[dragIndex];
                board[dragIndex] = '';
                if (isSingle) {
                    makeMoveSingle(dragIndex, idx);
                }
            }
            boardX[dragIndex] = dragIndex % 8 * FIELD;
            boardY[dragIndex] = Math.floor(dragIndex / 8) * FIELD;
        }
    };
    canvas.ontouchcancel = () => {
        isDrag = false;
        boardX[dragIndex] = dragIndex % 8 * FIELD;
        boardY[dragIndex] = Math.floor(dragIndex / 8) * FIELD;
    };
};
const getFieldIndex = (x, y) => {
    return Math.floor(x / FIELD) + Math.floor(y / FIELD) * 8;
};
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
        }
        else {
            if (num == 8) {
                fen += num;
                num = 0;
            }
            else {
                num++;
            }
        }
        if ((i + 1) % 8 == 0 && i < board.length - 1) {
            fen += (num != 0) ? num + "/" : "/";
            num = 0;
        }
    }
    if (num != 0) {
        fen += num;
    }
    console.log(fen);
    return fen;
};
//change
const makeMoveSingle = (start, target) => {
    validMoves = 0;
    let init;
    let url;
    let startPos = (isWhite) ? start : 63 - start;
    let targetPos = (isWhite) ? target : 63 - target;
    if (isSingle) {
        url = './chess/move/single?startPos=' + startPos + '&targetPos=' + targetPos;
    }
    else {
        url = './chess/move/multi?startPos=' + startPos + '&targetPos=' + targetPos;
    }
    let move = {
        startPos: (isWhite) ? start : 63 - start,
        targetPos: (isWhite) ? target : 63 - target
    };
    let players = `[{"playerId":${player.playerId}, "name":"${player.name}"}]`;
    console.log(players);
    init = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: players
    };
    fetch(url, init)
        .then(response => response.json())
        .then(json => {
        console.log(json["fenString"]);
        validMoves = json["moves"];
        lastStart = json["aiMove"].startPos;
        lastTarget = json["aiMove"].targetPos;
        turn = json["whiteTurn"];
        lastFen = json["fenString"];
        console.log(lastFen);
        setBoard(json["fenString"]);
    })
        .catch(error => console.log('error', error));
};
const drawValidFields = () => {
    let canvas = document.getElementById("canvas");
    let r = 15 / 2;
    let ctx = canvas.getContext("2d");
    for (let m in validMoves) {
        //console.log(validMoves[m].startPos + " " + dragIndex)
        let clickedIdx = (isWhite) ? dragIndex : 63 - dragIndex;
        if (validMoves[m].startPos == clickedIdx) {
            ctx.beginPath();
            let idx = validMoves[m].targetPos;
            let x = idx % 8 * FIELD + (FIELD / 2);
            let y = Math.floor(idx / 8) * FIELD + (FIELD / 2);
            if (board[(isWhite) ? idx : 63 - idx] == '') {
                ctx.fillStyle = "#6e6e42";
            }
            else {
                ctx.fillStyle = "#FF0000";
            }
            ctx.arc(x, y, r, 0, 2 * Math.PI);
            ctx.fill();
            //ctx.stroke()
            ctx.closePath();
        }
    }
};
const changeColor = async () => {
    isWhite = !isWhite;
    console.log(isWhite);
    console.log(lastFen);
    setBoard(lastFen);
    /*for(let key in validMoves){
        validMoves[key].startPos = 63 - validMoves[key].startPos;
        validMoves[key].targetPos = 63 - validMoves[key].targetPos;
        console.log(key);
    }*/
};
let player;
const loginPlayer = () => {
    let url = "./chess/login";
    let init = {
        method: 'POST',
        body: document.getElementById("name").value
    };
    init = {
        method: 'POST',
        body: "Maxi"
    };
    fetch(url, init)
        .then(response => response.json())
        .then(json => {
        player = json;
        console.log(json);
        document.getElementById("login").style.display = "none";
        document.getElementById("match").style.display = "block";
        let text = "Hallo " + player["name"] + "! (id: " + player["playerId"] + ")";
        document.getElementById("showName").innerText = text;
    })
        .catch(error => console.log('error', error));
};
