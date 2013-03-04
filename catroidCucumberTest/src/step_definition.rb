module Catrobat
  # Contains all information about the step-definition.
  class StepDefinition
    # Represents a parameter of the step-definition.
    class MethodParameter
      attr_reader(:type, :name)

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
end
