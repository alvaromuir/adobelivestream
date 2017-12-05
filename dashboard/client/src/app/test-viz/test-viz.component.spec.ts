import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestVizComponent } from './test-viz.component';

describe('TestVizComponent', () => {
  let component: TestVizComponent;
  let fixture: ComponentFixture<TestVizComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TestVizComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestVizComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
