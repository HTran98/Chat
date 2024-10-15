import { Component ,Input, EventEmitter, Output, SimpleChanges} from '@angular/core';

@Component({
  selector: 'app-custom-input',
  standalone: true,
  imports: [],
  templateUrl: './custom-input.component.html',
  styleUrl: './custom-input.component.css'
})
export class CustomInputComponent {
  @Input() value: string| undefined | null = ''; // Giá trị đầu vào
  @Output() valueChange: EventEmitter<string> = new EventEmitter<string>(); // Sự kiện thay đổi giá trị
  @Output() focusOut: EventEmitter<void> = new EventEmitter<void>(); // Sự kiện khi mất focus
  @Output() focus: EventEmitter<void> = new EventEmitter<void>(); // Sự kiện khi nhận focus
  @Output() change: EventEmitter<string> = new EventEmitter<string>(); // Sự kiện khi giá trị thay đổi
  internalValue: string = ''
  
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['value']) {
      this.internalValue = this.value ?? '';
    }
  }
  onInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.value = input.value;
    this.valueChange.emit(this.value);
  }

  onFocus(): void {
    this.focus.emit();
  }

  onFocusOut(): void {
    this.focusOut.emit();
  }

  onChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.change.emit(input.value);
  }
}
