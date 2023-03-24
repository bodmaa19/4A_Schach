import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModeButtonComponent } from './mode-button.component';

describe('ModeButtonComponent', () => {
  let component: ModeButtonComponent;
  let fixture: ComponentFixture<ModeButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModeButtonComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModeButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
