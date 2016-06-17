import sbt.Project.projectToRef

lazy val clients = Seq(exampleClient)
lazy val scalaV = "2.11.8"

//resolvers += "bintray/non" at "http://dl.bintray.com/non/maven"

lazy val exampleServer = (project in file("example-server")).settings(
  scalaVersion := scalaV,
  routesImport += "config.Routes._",
  scalaJSProjects := clients,
  pipelineStages := Seq(scalaJSProd, gzip),
  resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  libraryDependencies ++= Seq(
    filters,
    jdbc,
    evolutions,
    "com.typesafe.play" %% "anorm" % "2.5.0",
    "com.lihaoyi" %% "scalatags" % "0.5.4",
    "com.vmunier" %% "play-scalajs-scripts" % "0.3.0",
    "com.typesafe.slick" %% "slick" % "3.0.2",
    "com.typesafe.play" %% "play-slick" % "1.0.1",
    "com.lihaoyi" %% "upickle" % "0.4.0",
    "org.webjars" %% "webjars-play" % "2.4.0",
    "org.webjars" % "bootstrap" % "3.3.5",
    "org.webjars" % "jquery" % "2.1.4",
    "org.webjars" % "font-awesome" % "4.4.0"
  )
 ).enablePlugins(PlayScala).
  aggregate(clients.map(projectToRef): _*).
  dependsOn(exampleSharedJvm)

lazy val exampleClient = (project in file("example-client")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  resolvers += "jitpack" at "https://jitpack.io",
  persistLauncher in Test := false,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.1",
    "com.lihaoyi" %%% "scalatags" % "0.5.2",
    "com.lihaoyi" %%% "scalarx" % "0.2.8",
    "be.doeraene" %%% "scalajs-jquery" % "0.8.0",
    "com.lihaoyi" %%% "upickle" % "0.4.0",
    "com.github.fancellu" % "scalajs-vue" % "v0.1"
  ),
  jsDependencies ++= Seq( // todo: provide scoping for the production/minified versions
    RuntimeDOM,
    "org.webjars.bower" % "vue" % "1.0.21" / "dist/vue.js" minified "dist/vue.min.js",
    "org.webjars.bower" % "vue-router" % "0.7.13" / "dist/vue-router.js" minified "dist/vue-router.min.js" dependsOn "dist/vue.js"
  )
).enablePlugins(ScalaJSPlugin, ScalaJSPlay).
  dependsOn(exampleSharedJs)

lazy val exampleShared = (crossProject.crossType(CrossType.Pure) in file("example-shared")).
  settings(
    scalaVersion := scalaV,
    libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.3.4"
  ).
  jsConfigure(_ enablePlugins ScalaJSPlay)

lazy val exampleSharedJvm = exampleShared.jvm
lazy val exampleSharedJs = exampleShared.js

// loads the jvm project at sbt startup
onLoad in Global := (Command.process("project exampleServer", _: State)) compose (onLoad in Global).value