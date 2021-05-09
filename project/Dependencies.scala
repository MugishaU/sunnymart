import sbt.Keys._
import sbt.{Project, _}

object Dependencies {
  val awsJdkVersion = "1.11.996"

  val projectDependencies = Seq(
    "com.amazonaws" % "aws-java-sdk-dynamodb" % awsJdkVersion,
    "com.amazonaws" % "aws-java-sdk-s3" % awsJdkVersion
  )

  implicit class DependenciesProject(project: Project) {
    def withDependencies: Project =
      project
        .settings(
          libraryDependencies ++= projectDependencies
        )
  }

}
