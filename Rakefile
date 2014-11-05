

EXCLUDE_MODULES = %w|
 presto-cassandra presto-kafka presto-docs
 presto-mysql presto-postgresql presto-hive
 presto-hive-hadoop1 presto-hive-hadoop2
 presto-hive-cdh4 presto-hive-cdh5|

def presto_modules
  require "rexml/document"
  pom = REXML::Document.new(File.read("pom.xml"))
  modules = []
  REXML::XPath.each(pom, "/project/modules/module"){|m|
    modules << m.text
  }
  modules
end

def active_modules
  modules = []
  presto_modules.each{|m|
    m_name = m.text
    modules << m_name if EXCLUDE_MODULES.none? {|e| e == m_name }
  }
  modules
end

desc "compile codes"
task "compile" do
  sh "mvn test-compile"
end

desc "run tests"
task "test" do
  sh "mvn #{active_modules.join(",")} test"
end

desc "deploy presto"
task "deploy" do

  require "rexml/document"

  # Read the current presto version
  rev = `git rev-parse HEAD`
  pom = REXML::Document.new(File.read("pom.xml"))
  presto_version = REXML::XPath.first(pom, "/project/version")

  # Set (presto-version)-(git revision number:first 10 characters) version to pom.xml files
  version = "#{presto_version.text.gsub("-SNAPSHOT", "")}-#{rev[0...10]}"
  sh "mvn versions:set -DgenerateBackupPoms=false -DnewVersion=#{version}"

  # Reload pom.xml
  pom = REXML::Document.new(File.read("pom.xml"))

  # delete unncessary modules from pom.xml
  # EXCLUDE_MODULES.each{|m|
  #    pom.delete_element("/project/modules/module[text()='#{m}']")
  # }

  # Inject extension plugin to deploy artifacts to s3
  extension = <<EOF
    <extensions>
      <extension>
        <groupId>org.springframework.build</groupId>
        <artifactId>aws-maven</artifactId>
        <version>5.0.0.RELEASE</version>
      </extension>
    </extensions>
EOF
  REXML::XPath.first(pom, "/project/build").add_element(REXML::Document.new(extension))

  # Inject build profile for TD (disable version check, set distribution management tag)
  profiles = REXML::XPath.first(pom, "/project/profiles")
  profiles.add_element(REXML::Document.new(File.read("td-profile.xml")))

  # Dump pom.xml
  File.open('pom.xml', 'w'){|f| pom.write(f) }

  # Deploy
  target_modules = presto_modules.keep_if{|m| m != 'presto-docs'}
  sh "mvn deploy -P td -pl #{target_modules.join(",")} -DskipTests"

end
