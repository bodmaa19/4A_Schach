import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { HeaderComponent } from './header/header.component';
import { ModeButtonComponent } from './mode-button/mode-button.component';
import { SinglePlayerModeComponent } from './single-player-mode/single-player-mode.component';
import { MultiplayerModeComponent } from './multi-player-mode/multiplayer-mode.component';
import { SinglePlayerLevelComponent } from './single-player-level/single-player-level.component';
import { MultiPlayerLevelComponent } from './multi-player-level/multi-player-level.component';
import { GameComponent } from './game/game.component';
import { BoardComponent } from './board/board.component';
import { UserComponent } from './user/user.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    HeaderComponent,
    ModeButtonComponent,
    SinglePlayerModeComponent,
    MultiplayerModeComponent,
    SinglePlayerLevelComponent,
    MultiPlayerLevelComponent,
    GameComponent,
    BoardComponent,
    UserComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
