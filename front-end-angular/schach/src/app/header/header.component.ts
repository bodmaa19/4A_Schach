import {Component} from '@angular/core';
import {SchachService} from "../schach.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {
  logOut = (): void => {
    console.log(1442);
    // @ts-ignore
    sessionStorage.setItem("username", "notLoggedIn");
    // @ts-ignore
    sessionStorage.setItem("password", "notLoggedIn");
  }

  constructor(public schach : SchachService) {
  }

  isUserRoute(): boolean
  {
    return this.schach.router.url !== '/';
  }
}
