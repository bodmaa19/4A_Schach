import {Component, OnInit} from '@angular/core';
import {SchachService} from "../schach.service";

@Component({
  selector: 'app-multi-player-level',
  templateUrl: './multi-player-level.component.html',
  styleUrls: ['./multi-player-level.component.css']
})
export class MultiPlayerLevelComponent implements OnInit {
  constructor(public schach : SchachService) {
  }

  ngOnInit(): void {
  }
}
