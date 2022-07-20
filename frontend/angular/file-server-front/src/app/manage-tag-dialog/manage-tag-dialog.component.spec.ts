import { ComponentFixture, TestBed, waitForAsync } from "@angular/core/testing";

import { ManageTagDialogComponent } from "./manage-tag-dialog.component";

describe("ManageTagDialogComponent", () => {
  let component: ManageTagDialogComponent;
  let fixture: ComponentFixture<ManageTagDialogComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ManageTagDialogComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ManageTagDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
