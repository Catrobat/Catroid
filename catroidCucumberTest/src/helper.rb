require_relative "step_definition"
require_relative "java_parser"

module Catrobat
  module Helper
    def tranform_java_steps(java_dir = "features/step_definitions/java")
      helper = Helper.new
      Dir.glob(java_dir + "/*.java") do |java_file|
        parser           = Parser::JavaParser.new(java_file)
        step_definitions = parser.parse!
        step_definitions.each_with_index do |step_def, i|
          helper.write_java_file(step_def)
          helper.write_ruby_file(step_def, i)
        end
      end
    end

    private
    class Helper
      def write_java_file(step_def, dir = "catrobat")
        class_name = step_def.method_name.gsub("_", "").capitalize
        action_dir = "calabash-android/ruby-gem/test-server/instrumentation-backend/"
        action_dir<<"src/sh/calaba/instrumentationbackend/actions/"
        action_dir<<dir
        FileUtils.mkdir_p(action_dir)
        File.open("#{action_dir}/#{class_name}.java", 'w') do |f|
          write_java_text(f, class_name, step_def)
        end
      end

      def write_ruby_file(step_def, index, file_name="created_steps")
        steps_dir = "features/step_definitions"
        file_path = "#{steps_dir}/#{file_name}.rb"
        if index == 0
          File.open(file_path, 'w') do |f|
            f.write "# These steps were automatically created.\n\n"
          end
        end
        File.open(file_path, 'a') do |f|
          if index > 0
            f.write "\n"
          end
          write_ruby_text(f, step_def)
        end
      end

      private
      def write_java_text(f, class_name, step_def)
        f.write "package sh.calaba.instrumentationbackend.actions.button;\n\n"
        f.write "import sh.calaba.instrumentationbackend.InstrumentationBackend;\n"
        f.write "import sh.calaba.instrumentationbackend.Result;\n"
        f.write "import sh.calaba.instrumentationbackend.actions.Action;\n"
        f.write "import static sh.calaba.instrumentationbackend.InstrumentationBackend.solo;\n\n"
        f.write "public class #{class_name} implements Action {\n"
        f.write "  @Override\n"
        f.write "  public Result execute(String... args) {\n"
        f.write "    #{step_def.method_body}\n"
        f.write "    return Result.successResult();\n"
        f.write "  }\n\n"
        f.write "  @Override\n"
        f.write "  public String key() {\n"
        f.write "    return \"#{step_def.method_name}\";\n"
        f.write "  }\n"
        f.write "}\n"
      end

      private
      def write_ruby_text(f, step_def)
        args = list_args(step_def)
        f.write "#{step_def.type} /^#{step_def.text}$/ do#{block_args(args)}\n"
        f.write "  performAction('#{step_def.method_name}'#{action_args(args)})"
        f.write "\nend\n"
      end

      private
      def list_args(step_def)
        block_args = nil
        step_def.method_params.each do |param|
          if block_args
            block_args<<", #{param.name.downcase}"
          else
            block_args = "#{param.name.downcase}"
          end
        end
        block_args
      end

      private
      def block_args(args)
        args ? " |#{args}|" : nil
      end

      private
      def action_args(args)
        args ? ", #{args}" : nil
      end
    end
  end
end
