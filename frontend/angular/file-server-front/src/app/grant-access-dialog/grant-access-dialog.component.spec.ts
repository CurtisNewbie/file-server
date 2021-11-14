import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GrantAccessDialogComponent } from './grant-access-dialog.component';

describe('GrantAccessDialogComponent', () => {
  let component: GrantAccessDialogComponent;
  let fixture: ComponentFixture<GrantAccessDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GrantAccessDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GrantAccessDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
