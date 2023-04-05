import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {HomeComponent} from "./home/home.component";
import {SinglePlayerModeComponent} from "./single-player-mode/single-player-mode.component";
import {MultiplayerModeComponent} from "./multi-player-mode/multiplayer-mode.component";
import {GameComponent} from "./game/game.component";
import {UserComponent} from "./user/user.component";
import {SchachService} from "./schach.service";

const routes: Routes = [
  {
    path : '',
    component : UserComponent
  },
  {
    path : 'home',
    component : HomeComponent,
    canActivate: [SchachService]
  },
  {
    path : 'singlePlayerMode',
    component : SinglePlayerModeComponent,
    canActivate: [SchachService]
  },
  {
    path : 'multiPlayerMode',
    component : MultiplayerModeComponent,
    canActivate: [SchachService]
  },
  {
    path : 'game',
    component : GameComponent,
    canActivate: [SchachService]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
