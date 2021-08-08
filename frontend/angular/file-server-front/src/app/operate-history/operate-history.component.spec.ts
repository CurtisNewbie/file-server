import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OperateHistoryComponent } from './operate-history.component';

describe('OperateHistoryComponent', () => {
  let component: OperateHistoryComponent;
  let fixture: ComponentFixture<OperateHistoryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OperateHistoryComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OperateHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
