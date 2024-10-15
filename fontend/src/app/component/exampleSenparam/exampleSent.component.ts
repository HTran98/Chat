import { OnInit, Component } from '@angular/core';
import { Router } from '@angular/router';
import { ExampleService } from '../../service/example.service';
import { Category } from '../../model/Category';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CustomInputComponent } from '../custom-input/custom-input.component';
@Component({
  selector: 'exampleSent',
  standalone: true,
  templateUrl: './exampleSent.component.html',
  imports: [CommonModule, FormsModule,CustomInputComponent],
})
export class ExampleSentComponent implements OnInit {
  category: Category = new Category();
  valueInput: string= 'Test'
  constructor(private exampleService: ExampleService) {}
  ngOnInit(): void {

  }
  onSubmit() {
    
    this.exampleService.createCategory(this.category).subscribe((data) => {
      console.log(data);
    });
  }
  onFocusOut(){
    console.log(this.category,'this.value')
  }
}
