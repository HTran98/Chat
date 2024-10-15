import { routes } from './../../app.routes';

import { OnInit, Component } from "@angular/core"
import { Router } from '@angular/router';
import {ExampleService} from '../../service/example.service'
import { Category } from "../../model/Category";
import { CommonModule } from "@angular/common";
@Component({
  selector:'example',
  standalone: true,
  templateUrl:'./example.component.html',
  styleUrls: ['./example.component.css'],
  imports: [CommonModule]
})
export class ExampleComponent implements OnInit {
    categoryList : Category[] = [
      ];
    categorylst : Category[]= []
      category: Category = new Category()
    constructor(private exampleService: ExampleService, private router: Router) { }
    ngOnInit(): void {
        this.exampleService.getData(this.category).subscribe(data => {
           this.categoryList = data
        });
        this.category.categoryName="test"
        this.exampleService.getDataList(this.category).subscribe(data => {
          this.categorylst = data
          console.log(this.categorylst)
        })
      }
    goto(){
      this.router.navigate(['send-data'])
    }
}