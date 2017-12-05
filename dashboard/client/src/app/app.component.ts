import { Component } from '@angular/core';
import {LiveStreamService} from "./livestream.service";
import {WebSocketService} from "./websocket.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  // constructor(private liveStreamService: LiveStreamService) {
  //   liveStreamService.messages.subscribe(msg => {
  //     console.log(msg.message);
  //   });
  // }
}
