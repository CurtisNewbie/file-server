import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ManageTasksComponent } from './manage-tasks.component';

describe('ManageTasksComponent', () => {
  let component: ManageTasksComponent;
  let fixture: ComponentFixture<ManageTasksComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ManageTasksComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ManageTasksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
