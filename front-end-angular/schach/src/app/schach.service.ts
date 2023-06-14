import {Injectable, OnInit} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from "@angular/router";
import {animate} from "@angular/animations";

@Injectable({
  providedIn: 'root'
})
export class SchachService implements OnInit, CanActivate {
  links : String[] = [
    "game",
    "game"
  ];

  modes : String[] = [
    "Singleplayer",
    "Multiplayer"
  ];

  modeStyles : String[] = [
    "color : #825324; border-color: #825324",
    "color : #e3c6aa; border-color: #e3c6aa"
  ];

  playerStatus : String[] = [
    "online",
    "offline"
  ];

  playerStatusStyles : String[] = [
    "color : #417b32",
    "color : #8d2323"
  ];

  singlePlayerLevel : String[] = [
    "Leicht",
    "Mittel",
    "Schwer"
  ];

  token : string = "";
  player : any;
  opponentPlayer : any;
  multiPlayerPlayers : any[] = [];
  isMultiPlayer : boolean = false;

  register = () : void =>
  {
    // @ts-ignore
    let username : String = document.getElementById("username").value;
    // @ts-ignore
    let password : String = document.getElementById("password").value;
    let user = {
      userId: 0,
      username: username,
      password: password
    };
    let init = {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(user)
    };
    let url : URL = new URL('http://localhost:8080/userController/register');
    fetch(url, init).then(response =>
    {
      if (!response.ok)
      {
        throw new Error("failed to register with http status " + response.statusText);
      }
      alert("You have successfully registered and now you are able to login!");
    }).catch(error => alert(error.toString()));
  }

  login = (name ?: string, pwd ?: string) : void =>
  {
    let username : String = "";
    let password : String = "";
    console.log(name)
    if(name == undefined && pwd == undefined){
      // @ts-ignore
      console.log(document.getElementById("username").value)
      username = (<HTMLInputElement> document.getElementById("username")).value;
      password = (<HTMLInputElement> document.getElementById("password")).value;
    }else{
      // @ts-ignore
      username = name;
      // @ts-ignore
      password = pwd;
    }
    console.log(username)
    let user = {
      userId: 0,
      username: username,
      password: password
    };
    let init = {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(user)
    };
    let url : URL = new URL('http://localhost:8080/userController/login');
    fetch(url, init).then(response =>
    {
      if (!response.ok)
      {
        throw new Error("failed to login with http status " + response.statusText);
      }
      return response.json();
    }).then(json =>
    {
      this.token = json["token"];
      // @ts-ignore
      this.player = json;
      console.log(json)
      this.router.navigate(['/home']);
      // @ts-ignore
      sessionStorage.setItem("username", username);
      // @ts-ignore
      sessionStorage.setItem("password", password);
    }).catch(error => console.log(error.toString()));
  }

  setIsMultiPlayer = (mode : String) : void =>
  {
    if (mode == "Singleplayer")
    {
      this.setOpponentPlayer(
        {
          username: "Maxi_123",
          best_score: 10000
        }
      );
      this.isMultiPlayer = false;
    }
    else
    {
      this.loadMultiPlayers();
      this.setOpponentPlayer(
        {
          username: "Maxi is waiting for a player",
          best_score: 10000
        }
      );
      this.isMultiPlayer = true;
    }
  }

  loadMultiPlayers = () : void =>
  {
    let init = {
      method: 'GET'
    };
    let url : URL = new URL('http://localhost:8080/userController/users');
    fetch(url, init).then(response =>
    {
      if (!response.ok)
      {
        throw new Error("failed to load multiplayer players with http status " + response.statusText);
      }
      return response.json();
    }).then(json =>
    {
      // @ts-ignore
      this.multiPlayerPlayers = json;
    }).catch(error => console.log(error.toString()));
  }

  setOpponentPlayer = (opponentPlayer : any) : void =>
  {
    this.opponentPlayer = opponentPlayer;
  }

  constructor(public router : Router) { }

  ngOnInit(): void {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    let init = {
      method: 'GET',
      headers: {
        "Authorization": this.token
      }
    };
    let url : URL = new URL('http://localhost:8080/userController/validToken');
    fetch(url, init).then(response =>
    {
      if (!response.ok)
      {
        throw new Error("failed to access resources because of invalid token with http status " + response.statusText);
      }
    }).catch(error =>
    {
      // alert(error.toString());
      this.router.navigate(['/']);
      return false;
    });
    return true;
  }
}
