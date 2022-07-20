import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ManageExtensionComponent } from './manage-extension.component';

describe('ManageExtensionComponent', () => {
  let component: ManageExtensionComponent;
  let fixture: ComponentFixture<ManageExtensionComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ManageExtensionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ManageExtensionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
