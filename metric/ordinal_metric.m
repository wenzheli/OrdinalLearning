function ordinal_metric(name_data)

% 1: nur   2: car    3: balance  4: syn

name_data = 1;

addpath('D:\workplace\OrdinalLearning\metric');

split = 1;  % first run

if name_data == 1
    load('nur_data.mat');
    
    [trX, trY, vaX, vaY, teX, teY] = get_split_data(all_X, all_Y, train_idx, valid_idx, test_idx, split);
    
    data = trX;
    y = trY;
    
    [val,idx] = sort(y);
    data = data(idx,:);
    y = y(idx);
    
    tst_data = teX;
    tst_y = teY;
    
    va_data = vaX;
    va_y = vaY;
    
elseif name_data == 2
    load('car_data.mat');
    
    [trX, trY, vaX, vaY, teX, teY] = get_split_data(all_X, all_Y, train_idx, valid_idx, test_idx, split);
    
    data = trX;
    y = trY;
    
    [val,idx] = sort(y);
    data = data(idx,:);
    y = y(idx);
    
    tst_data = teX;
    tst_y = teY;
    
    va_data = vaX;
    va_y = vaY;
    
    data(:,3) = data(:,3)-1;
    data(:,4) = data(:,4)/2;
    
    tst_data(:,3) = tst_data(:,3)-1;
    tst_data(:,4) = tst_data(:,4)/2;
    
    va_data(:,3) = va_data(:,3) - 1;
    va_data(:,4) = va_data(:,4)/2;
elseif name_data == 3
    load('bal_data.mat');
    
    [trX, trY, vaX, vaY, teX, teY] = get_split_data(all_X, all_Y, train_idx, valid_idx, test_idx, split);
    
    data = trX;
    y = trY;
    
    [val,idx] = sort(y);
    data = data(idx,:);
    y = y(idx);
    
    tst_data = teX;
    tst_y = teY;
    
    va_data = vaX;
    va_y = vaY;
end

% suppose all the attributes are ordinal
N = size(data,1);
D = size(data,2);
K = size(unique(y),1);

% normalize data
for j = 1:D
    max_val =  max( data(:,j) );
    data(:,j) = data(:,j) / max_val;
    va_data(:,j) = va_data(:,j) / max_val;
    tst_data(:,j) = tst_data(:,j) / max_val;
end

% baseline - Euclidean
errEuc = knncl(eye(D), data', y', tst_data', tst_y', 3);
fprintf('Euclidean: training %f\t test %f\n', errEuc(1), errEuc(2));

% baseline - LMNN
L0 = eye(D);
L = lmnn2(data', y', 3, L0, 'maxiter',1000, 'quiet', 1);
errLMNN = knncl(L, data', y', tst_data', tst_y', 3);
fprintf('LMNN: training %f\t test %f\n', errLMNN(1), errLMNN(2));

% our method
MAX_ITER = 10;

% initialize end points
intervals = cell(D,1);

for i = 1:D
    % # of ordinal values for each dimension
    n = length(unique(data(:,i)));
    
    interval = linspace(1/(2*n), (2*n+1)/(2*n), n+1);
    intervals{i} = interval;
end

% get target neighbors and imposters

for iter = 1:MAX_ITER
    % fix end points, learn L
    L = learn_L_fix_end_point(intervals, L, data, y);
    
    % fix L, search for end points
    % such that the validation accuracy is max
    intervals = update_interval( intervals, L, data, y );
end

fname = ['result/Data_', num2str(name_data), '_res.mat'];
save(fname, '');