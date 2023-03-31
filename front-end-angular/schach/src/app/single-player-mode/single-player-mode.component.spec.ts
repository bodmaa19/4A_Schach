import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SinglePlayerModeComponent } from './single-player-mode.component';

describe('SinglePlayerModeComponent', () => {
  let component: SinglePlayerModeComponent;
  let fixture: ComponentFixture<SinglePlayerModeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SinglePlayerModeComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SinglePlayerModeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
