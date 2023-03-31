import {Component, OnInit} from '@angular/core';
import {SchachService} from "../schach.service";

@Component({
  selector: 'app-single-player-level',
  templateUrl: './single-player-level.component.html',
  styleUrls: ['./single-player-level.component.css']
})
export class SinglePlayerLevelComponent implements OnInit {
  constructor(public schach : SchachService) {
  }

  ngOnInit(): void {
  }
}
