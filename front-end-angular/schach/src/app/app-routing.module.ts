import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {HomeComponent} from "./home/home.component";
import {SinglePlayerModeComponent} from "./single-player-mode/single-player-mode.component";
import {MultiplayerModeComponent} from "./multi-player-mode/multiplayer-mode.component";

const routes: Routes = [
  {
    path : '',
    component : HomeComponent
  },
  {
    path : 'singlePlayerMode',
    component : SinglePlayerModeComponent
  },
  {
    path : 'multiPlayerMode',
    component : MultiplayerModeComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
