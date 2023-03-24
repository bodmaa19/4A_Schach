import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MultiPlayerLevelComponent } from './multi-player-level.component';

describe('MultiPlayerLevelComponent', () => {
  let component: MultiPlayerLevelComponent;
  let fixture: ComponentFixture<MultiPlayerLevelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MultiPlayerLevelComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MultiPlayerLevelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
