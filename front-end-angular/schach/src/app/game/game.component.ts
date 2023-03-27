import {Component, OnInit} from '@angular/core';
import {SchachService} from "../schach.service";

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
