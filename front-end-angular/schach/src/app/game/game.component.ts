import {Component, OnInit} from '@angular/core';
import {SchachService} from "../schach.service";
import {BoardComponent} from "../board/board.component";

@Component({
  selector: 'app-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.css']
})
export class GameComponent implements OnInit {
  constructor(public schach : SchachService) {
  }

  ngOnInit(): void {
  }
}
