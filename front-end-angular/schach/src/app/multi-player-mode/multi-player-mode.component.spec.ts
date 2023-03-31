import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MultiplayerModeComponent } from './multiplayer-mode.component';

describe('MultiplayerModeComponent', () => {
  let component: MultiplayerModeComponent;
  let fixture: ComponentFixture<MultiplayerModeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MultiplayerModeComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MultiplayerModeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
