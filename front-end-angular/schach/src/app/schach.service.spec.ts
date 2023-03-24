import { TestBed } from '@angular/core/testing';

import { SchachService } from './schach.service';

describe('SchachService', () => {
  let service: SchachService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SchachService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
