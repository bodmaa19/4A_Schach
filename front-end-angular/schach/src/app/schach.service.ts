import {Injectable, OnInit} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from "@angular/router";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class SchachService implements OnInit, CanActivate {
  links : String[] = [
    "game",
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
  player : any = {
    username: "test"
  };
  opponentPlayer : any;
  multiPlayerPlayers : any[] = [
    {
      username: "Mani_123"
    },
    {
      username: "Maxi_123"
    },
    {
      username: "Roman_123"
    }
  ];

  register = () : void =>
  {
    // @ts-ignore
    let username : String = document.getElementById("username").value;
    // @ts-ignore
    let password : String = document.getElementById("password").value;
    let user = {
      username: username,
      password: password
    };
    let init = {
      method: 'POST',
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

  login = () : void =>
  {
// @ts-ignore
    let username : String = document.getElementById("username").value;
    // @ts-ignore
    let password : String = document.getElementById("password").value;
    let user = {
      username: username,
      password: password
    };
    let init = {
      method: 'POST',
      body: JSON.stringify(user)
    };
    let url : URL = new URL('http://localhost:8080/userController/login');
    fetch(url, init).then(response =>
    {
      if (!response.ok)
      {
        throw new Error("failed to login with http status " + response.statusText);
      }
      // @ts-ignore
      this.player = response.json();
      // @ts-ignore
      this.token = response.headers.get("Authorization");
      console.log(this.player)
      console.log(this.token)
      this.router.navigate(['/home']);
    }).catch(error => alert(error.toString()));
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
    }
    else
    {
      this.loadMultiPlayers();
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
      // @ts-ignore
      this.multiPlayerPlayers = response.json();
      console.log(this.multiPlayerPlayers)
    }).catch(error => alert(error.toString()));
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
      return true;
    }).catch(error => alert(error.toString()));
    this.router.navigate(['/']);
    return false;
  }
}
