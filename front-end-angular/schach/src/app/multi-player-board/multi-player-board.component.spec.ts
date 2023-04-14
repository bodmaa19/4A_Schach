import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MultiPlayerBoardComponent } from './multi-player-board.component';

describe('MultiPlayerBoardComponent', () => {
  let component: MultiPlayerBoardComponent;
  let fixture: ComponentFixture<MultiPlayerBoardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MultiPlayerBoardComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MultiPlayerBoardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
