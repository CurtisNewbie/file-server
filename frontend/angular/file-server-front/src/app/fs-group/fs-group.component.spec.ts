import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { FsGroupComponent } from './fs-group.component';

describe('FsGroupComponent', () => {
  let component: FsGroupComponent;
  let fixture: ComponentFixture<FsGroupComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ FsGroupComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FsGroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
