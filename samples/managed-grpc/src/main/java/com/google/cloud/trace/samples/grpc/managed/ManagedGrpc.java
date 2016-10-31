// Copyright 2016 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.trace.samples.grpc.managed;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.trace.core.TraceSink;
import com.google.cloud.trace.core.DefaultTraceContextHandler;
import com.google.cloud.trace.ManagedTracer;
import com.google.cloud.trace.TraceContextFactoryTracer;
import com.google.cloud.trace.core.TraceContextHandler;
import com.google.cloud.trace.TraceContextHandlerTracer;
import com.google.cloud.trace.Tracer;
import com.google.cloud.trace.grpc.v1.GrpcTraceConsumer;
import com.google.cloud.trace.core.ConstantTraceOptionsFactory;
import com.google.cloud.trace.core.JavaTimestampFactory;
import com.google.cloud.trace.core.StackTrace;
import com.google.cloud.trace.core.ThrowableStackTraceHelper;
import com.google.cloud.trace.core.TimestampFactory;
import com.google.cloud.trace.core.SpanContextFactory;
import com.google.cloud.trace.v1.TraceSinkV1;
import com.google.cloud.trace.v1.consumer.TraceConsumer;
import com.google.cloud.trace.v1.producer.TraceProducer;

import java.io.IOException;

public class ManagedGrpc {
  public static void main(String[] args) throws IOException {
    String projectId = System.getProperty("projectId");

    // Create the trace sink.
    TraceProducer traceProducer = new TraceProducer();
    TraceConsumer traceConsumer = new GrpcTraceConsumer("cloudtrace.googleapis.com",
        GoogleCredentials.getApplicationDefault());
    TraceSink traceSink = new TraceSinkV1(projectId, traceProducer, traceConsumer);

    // Create the tracer.
    SpanContextFactory spanContextFactory = new SpanContextFactory(
        new ConstantTraceOptionsFactory(true, false));
    TimestampFactory timestampFactory = new JavaTimestampFactory();
    Tracer tracer = new TraceContextFactoryTracer(traceSink, spanContextFactory, timestampFactory);

    // Create the managed tracer.
    TraceContextHandler traceContextHandler = new DefaultTraceContextHandler(
        spanContextFactory.initialContext());
    ManagedTracer managedTracer = new TraceContextHandlerTracer(tracer, traceContextHandler);

    // Create some trace data.
    managedTracer.startSpan("my span 1");

    managedTracer.startSpan("my span 2");

    StackTrace.Builder stackTraceBuilder = ThrowableStackTraceHelper.createBuilder(new Exception());
    managedTracer.setStackTrace(stackTraceBuilder.build());
    managedTracer.endSpan();

    managedTracer.endSpan();
  }
}
