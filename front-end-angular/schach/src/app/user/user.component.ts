import {Component, OnInit} from '@angular/core';
import {SchachService} from "../schach.service";

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {
  constructor(public schach : SchachService) {
  }

  ngOnInit(): void {
    // @ts-ignore
    let username : string = sessionStorage.getItem("username");
    // @ts-ignore
    let password : string = sessionStorage.getItem("password");
    if((username != "notLoggedIn" && password != "notLoggedIn")){
      this.schach.login(username, password);
    }
  }
}
