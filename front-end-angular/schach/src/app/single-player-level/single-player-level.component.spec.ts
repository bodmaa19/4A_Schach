import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SinglePlayerLevelComponent } from './single-player-level.component';

describe('SinglePlayerLevelComponent', () => {
  let component: SinglePlayerLevelComponent;
  let fixture: ComponentFixture<SinglePlayerLevelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SinglePlayerLevelComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SinglePlayerLevelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
