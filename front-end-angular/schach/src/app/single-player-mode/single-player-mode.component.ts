import {Component, OnInit} from '@angular/core';
import {SchachService} from "../schach.service";

@Component({
  selector: 'app-single-player-mode',
  templateUrl: './single-player-mode.component.html',
  styleUrls: ['./single-player-mode.component.css']
})
export class SinglePlayerModeComponent implements OnInit {
  public index : number = 0;

  constructor(public schach : SchachService) {
  }

  ngOnInit(): void {
  }
}
