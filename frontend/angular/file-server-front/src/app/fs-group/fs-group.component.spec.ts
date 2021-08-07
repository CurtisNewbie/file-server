import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FsGroupComponent } from './fs-group.component';

describe('FsGroupComponent', () => {
  let component: FsGroupComponent;
  let fixture: ComponentFixture<FsGroupComponent>;

  beforeEach(async(() => {
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
