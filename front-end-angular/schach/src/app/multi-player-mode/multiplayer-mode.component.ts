import {Component, OnInit} from '@angular/core';
import {SchachService} from "../schach.service";

@Component({
  selector: 'app-multiplayer-mode',
  templateUrl: './multi-player-mode.component.html',
  styleUrls: ['./multi-player-mode.component.css']
})
export class MultiplayerModeComponent implements OnInit {
  public index : number = 1;

  constructor(public schach : SchachService) {
  }

  ngOnInit(): void {
  }
}
