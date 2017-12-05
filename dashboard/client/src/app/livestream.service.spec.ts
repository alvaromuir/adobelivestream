import { TestBed, inject } from '@angular/core/testing';

import { LiveStreamService } from './livestream.service';

describe('LivestreamService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [LiveStreamService]
    });
  });

  it('should be created', inject([LiveStreamService], (service: LiveStreamService) => {
    expect(service).toBeTruthy();
  }));
});
