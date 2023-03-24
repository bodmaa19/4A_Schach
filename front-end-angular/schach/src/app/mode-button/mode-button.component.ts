import {Component, Input, OnInit} from '@angular/core';
import {SchachService} from "../schach.service";

@Component({
  selector: 'app-mode-button',
  templateUrl: './mode-button.component.html',
  styleUrls: ['./mode-button.component.css']
})
export class ModeButtonComponent implements OnInit {
  @Input() index : number = 0;

  constructor(public schach : SchachService) {
  }

  ngOnInit(): void {
  }
}
