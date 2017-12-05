import { Injectable } from '@angular/core';
import { Subject } from 'rxjs/Rx';
import { WebSocketService } from './websocket.service';

const STREAM_URL = 'ws://localhost:9000/socket';

export interface Message {
  source: string;
  message: string;
}

@Injectable()
export class LiveStreamService {
  public messages: Subject<Message>;

  constructor(wsService: WebSocketService) {
    this.messages = <Subject<Message>>wsService
    .connect(STREAM_URL)
    .map((response: MessageEvent): Message => {
      const data = JSON.parse(response.data);
      return {
        source: STREAM_URL,
        message: data
      };
    });
  }
}
