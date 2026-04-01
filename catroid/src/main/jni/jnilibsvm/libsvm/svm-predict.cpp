#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "../common.h"
#include "svm-predict.h"
#include "svm.h"

namespace svmpredict {
	int print_null(const char *s,...) {return 0;}

	//static int (*info)(const char *fmt,...) = &printf;
	//static void (*info)(const char *fmt,...) = &debug;

	struct svm_node *x;
	int max_nr_attr = 64;

	struct svm_model* model;
	int predict_probability=0;

	static char *line = NULL;
	static int max_line_len;

	static char* readline(FILE *input)
	{
		int len;

		if(fgets(line,max_line_len,input) == NULL)
			return NULL;

		while(strrchr(line,'\n') == NULL)
		{
			max_line_len *= 2;
			line = (char *) realloc(line,max_line_len);
			len = (int) strlen(line);
			if(fgets(line+len,max_line_len-len,input) == NULL)
				break;
		}
		return line;
	}

	void exit_input_error(int line_num)
	{
		debug("Wrong input format at line %d\n", line_num);
		exit(1);
	}

	void predict(float *input, int len, int *index, double *prob)
	{

		int svm_type=svm_get_svm_type(model);
		int nr_class=svm_get_nr_class(model);
		double *prob_estimates=NULL;
		int j;

		if(predict_probability)
		{
			if (svm_type==NU_SVR || svm_type==EPSILON_SVR)
				debug("Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma=%g\n",svm_get_svr_probability(model));
			else
			{
				prob_estimates = (double *) malloc(nr_class*sizeof(double));
			}
		}

        x = (struct svm_node *) malloc((len+1)*sizeof(struct svm_node));

        for (int i = 0; i < len; i++) {
            x[i].index = i;
            x[i].value = input[i];
        }
        x[len].index = -1;

        double predict_label;
        if (predict_probability && (svm_type==C_SVC || svm_type==NU_SVC))
        {
            predict_label = svm_predict_probability(model,x,prob_estimates);
            *index = (int) predict_label;
            *prob = prob_estimates[*index];
        }
        else
        {
            predict_label = svm_predict(model,x);
            *index = (int) predict_label;
            *prob = 0.0;
        }

		if(predict_probability)
			free(prob_estimates);
	}

	void exit_with_help()
	{
		debug(
		"Usage: svm-predict [options] test_file model_file output_file\n"
		"options:\n"
		"-b probability_estimates: whether to predict probability estimates, 0 or 1 (default 0); for one-class SVM only 0 is supported\n"
		"-q : quiet mode (no outputs)\n"
		);
		exit(1);
	}

	int main(int argc, char **argv, float *input, int len, int *index, double *prob)
	{
		int i;
		// parse options
		for(i=1;i<argc;i++)
		{
			if(argv[i][0] != '-') break;
			++i;
			switch(argv[i-1][1])
			{
				case 'b':
					predict_probability = atoi(argv[i]);
					break;
				case 'q':
					//info = &print_null;
					i--;
					break;
				default:
					debug("Unknown option: -%c\n", argv[i-1][1]);
					exit_with_help();
			}
		}

		if(i>=argc)
			exit_with_help();

		if((model=svm_load_model(argv[i]))==0)
		{
			debug("can't open model file %s\n",argv[i]);
			exit(1);
		}

		x = (struct svm_node *) malloc(max_nr_attr*sizeof(struct svm_node));
		if(predict_probability)
		{
			if(svm_check_probability_model(model)==0)
			{
				debug("Model does not support probabiliy estimates\n");
				exit(1);
			}
		}
		else
		{
			if(svm_check_probability_model(model)!=0)
				debug("Model supports probability estimates, but disabled in prediction.\n");
		}

		predict(input,len,index,prob);
		svm_free_and_destroy_model(&model);
		free(x);
		free(line);
		return 0;
	}
}
