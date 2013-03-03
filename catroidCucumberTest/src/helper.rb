# helper.rb

module Catrobat
  module Helper
    # Contains all information about the step-definition.
    class StepDefinition
      # Represents a parameter of the step-definition.
      class MethodParameter
        attr_accessor(:type, :name)

        def initialize(type, name)
          @type = type
          @name = name
        end
      end

      attr_accessor(:type, :text, :method_name, :method_params, :method_body)

      def initialize(type, text)
        @type          = type
        @text          = text
        @method_name   = nil
        @method_params = []
        @method_body   = ""
      end
    end

    # Very basic parser to parse step-definitions written in Java.
    # Does not work with nested blocks or fancy formatting other than in the example.
    class JavaParser
      STEP_REGEX       = /@\b(Given|When|Then|And)\("(.*)"\)/
      METHOD_TOP_REGEX = /public\svoid\s(\w+)\(([^)]*)\)\s?\{\n/
      METHOD_END_REGEX = /\s+}/
      PARAM_REGEX      = /(\w+)\s(\w+),?/

      def initialize(path)
        raise ArgumentError unless File.exists?(path)
        @java_file       = File.open(path)
        @all_definitions = []
      end

      def parse!
        @java_file.each_line do |line|
          if (step = line.match(STEP_REGEX))
            @step_definition = StepDefinition.new(step[1], step[2])
            next
          end
          if (method = line.match(METHOD_TOP_REGEX)) and @step_definition
            @step_definition.method_name=method[1]
            # Find all MatchData for the method parameters.
            if method[2] and (params = method[2].to_enum(:scan, PARAM_REGEX).map { Regexp.last_match })
              params.each do |param|
                @step_definition.method_params<<StepDefinition::MethodParameter.new(param[1], param[2])
              end
            end
            next
          end
          if @step_definition and @step_definition.method_name
            if line.match(METHOD_END_REGEX)
              @all_definitions<<@step_definition
              @step_definition = nil
            else
              @step_definition.method_body<<line
            end
            next
          end
        end
        @all_definitions
      end
    end

    def tranform_java_steps(java_dir = "features/step_definitions/java")
      #TODO: create corresponding Actions in calabash-android, write created_steps.rb.

      #mapping = {"String" => "",
      #           "float"  => "Float.parseFloat",
      #           "int"    => "Integer.parseInt"}

      Dir.glob(java_dir + "/*.java") do |java_file|
        parser           = JavaParser.new(java_file)
        step_definitions = parser.parse!
        step_definitions.each do |definition|
          print_def(definition)
        end
      end
    end

    # For testing purposes only
    def print_def(definition)
      puts "#{definition.type.upcase} '#{definition.text}' ##{definition.method_name}(#{definition.method_params.size})"
      puts definition.method_body
    end
  end
end
