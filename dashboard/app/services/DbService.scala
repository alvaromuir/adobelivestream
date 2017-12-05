package services

import javax.inject.{Inject, Singleton}

import play.api.{Configuration, Logger}
import play.api.inject.ApplicationLifecycle

@Singleton
class DbService @Inject() (configuration: Configuration, appLifecycle: ApplicationLifecycle) {
  Logger.info(s"Starting Kafka service...")


}
