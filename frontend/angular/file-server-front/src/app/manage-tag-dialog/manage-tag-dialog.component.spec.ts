import { async, ComponentFixture, TestBed } from "@angular/core/testing";

import { ManageTagDialogComponent } from "./manage-tag-dialog.component";

describe("ManageTagDialogComponent", () => {
  let component: ManageTagDialogComponent;
  let fixture: ComponentFixture<ManageTagDialogComponent>;

  beforeEach(async(() => {
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
