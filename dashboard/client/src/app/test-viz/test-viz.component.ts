import { Component, OnInit, OnChanges, ViewChild, ElementRef, ViewEncapsulation } from '@angular/core';
import * as d3 from 'd3';
import {LiveStreamService} from "../livestream.service";
import {WebSocketService} from "../websocket.service";

@Component({
  selector: 'app-test-viz',
  templateUrl: './test-viz.component.html',
  styleUrls: ['./test-viz.component.css'],
  encapsulation: ViewEncapsulation.None,
  providers: [ WebSocketService, LiveStreamService ]
})
export class TestVizComponent implements OnInit, OnChanges {
  @ViewChild('chart') private chartContainer: ElementRef;

  constructor(private liveStreamService: LiveStreamService) {
    liveStreamService.messages.subscribe(msg => {
        console.log(msg.message['browser']);
    });

    liveStreamService.messages.unsubscribe()
  }

  ngOnInit() {
    this.createChart()
  }
  ngOnChanges() {}


  createChart() {
    var dataset = [ 8, 10, 12, 9, 7, 8, 10 ];

    d3.select('.test-viz').selectAll('#chart')
      .data(dataset)
      .enter()
      .append('div')
      .attr('class', 'bar')
      .style('height', function (d) {
        return d * 5 + 'px';
      });
  }
}
