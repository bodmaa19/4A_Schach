import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {HomeComponent} from "./home/home.component";
import {SinglePlayerModeComponent} from "./single-player-mode/single-player-mode.component";
import {MultiplayerModeComponent} from "./multi-player-mode/multiplayer-mode.component";
import {GameComponent} from "./game/game.component";
import {UserComponent} from "./user/user.component";

const routes: Routes = [
  {
    path : '',
    component : UserComponent
  },
  {
    path : 'home',
    component : HomeComponent
  },
  {
    path : 'singlePlayerMode',
    component : SinglePlayerModeComponent
  },
  {
    path : 'multiPlayerMode',
    component : MultiplayerModeComponent
  },
  {
    path : 'game',
    component : GameComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
