module Catrobat
  module Parser
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
        @all_definitions unless @all_definitions.empty?
        @java_file.each_line do |line|
          # Parse step-definition type and text.
          if (step = line.match(STEP_REGEX))
            text             = step[2].gsub("\\\\", "\\") # Replace all occurences of '\\' with '\' in the Java-regex.
            @step_definition = StepDefinition.new(step[1], text)
            next
          end
          # Parse step-definition method name and parameters.
          if (method = line.match(METHOD_TOP_REGEX)) and @step_definition
            @step_definition.method_name=method[1]
            # For each PARAM_REGEX match store the MatchData in params.
            if method[2] and (params = method[2].to_enum(:scan, PARAM_REGEX).map { Regexp.last_match })
              params.each do |param|
                @step_definition.method_params<<StepDefinition::MethodParameter.new(param[1], param[2])
              end
            end
            next
          end
          # Either store a complete step-definition or read a line of the method body.
          if @step_definition and @step_definition.method_name
            if line.match(METHOD_END_REGEX)
              @all_definitions<<@step_definition
              @step_definition = nil
            else
              # Replace any parameter references with a resolved reference of the 'String... args' parameter.
              @step_definition.method_params.each_with_index do |param, i|
                line.gsub!(param.name, parameter_to_arg_string(param.type, i))
              end
              @step_definition.method_body<<line.strip
            end
            next
          end
        end
        @all_definitions
      end

      private
      def parameter_to_arg_string(type, i)
        case type
          when "int"
            "Integer.parseInt(args[#{i}])"
          when "float"
            "Float.parseFloat(args[#{i}])"
          when "double"
            "Double.parseDouble(args[#{i}])"
          else
            "args[#{i}]"
        end
      end
    end
  end
end
