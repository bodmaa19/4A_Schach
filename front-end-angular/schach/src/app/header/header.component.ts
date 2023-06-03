import {Component} from '@angular/core';

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
}
