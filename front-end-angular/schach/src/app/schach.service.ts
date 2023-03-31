import {Injectable, OnInit} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SchachService implements OnInit {
  links : String[] = [
    "singlePlayerMode",
    "multiPlayerMode"
  ];

  modes : String[] = [
    "Singleplayer",
    "Multiplayer"
  ];

  modeStyles : String[] = [
    "color : #825324; border-color: #825324",
    "color : #e3c6aa; border-color: #e3c6aa"
  ];

  singlePlayerLevel : String[] = [
    "Leicht",
    "Mittel",
    "Schwer"
  ];

  multiPlayerPlayers : String[] = [
    "Maxi_123",
    "Maxi_123",
    "Maxi_123",
    "Maxi_123",
    "Maxi_123",
    "Maxi_123",
    "Maxi_123",
    "Maxi_123",
    "Maxi_123",
    "Maxi_123",
    "Maxi_123",
    "Maxi_123"
  ];

  playerStatus : String[] = [
    "online",
    "offline"
  ];

  playerStatusStyles : String[] = [
    "color : #417b32",
    "color : #8d2323"
  ];

  isMultiPlayer : boolean = false;

  singlePlayers : String[] = [
    "Maxi_123",
    "Ich"
  ];

  multiPlayers : String[] = [
    "Maxi_123",
    "Ich"
  ];

  register = () : void =>
  {

  }

  login = () : void =>
  {

  }

  constructor() { }

  ngOnInit(): void {
  }
}
