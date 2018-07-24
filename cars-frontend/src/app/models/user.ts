import {Car} from "./car";


export class User {
  public name: string;
  public email: string;
  public password: string;
  public cars: Car[];

  constructor(name?: string, email?: string, password?: string) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.cars = [];
  }
}
