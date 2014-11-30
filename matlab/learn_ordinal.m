function learn_ordinal

name_data = 1; % 1: nur   2: car

addpath('C:\Users\Yuan Shi\Desktop\ordinal_svm\liblinear-1.96\matlab');
addpath('C:\Users\Yuan Shi\Desktop\ordinal_svm\libknn2\mexfunctions');
addpath('C:\Users\Yuan Shi\Desktop\ordinal_svm\libknn2\helperfunctions');
addpath('C:\Users\Yuan Shi\Desktop\ordinal_svm\libknn2');

if name_data == 1
    load('nur_data2.mat');
    
    data = trX;
    y = trY;
    
    [val,idx] = sort(y);
    data = data(idx,:);
    y = y(idx);
    
    tst_data = teX;
    tst_y = teY;
else
    load('car_data.mat');
    
    data = trX;
    y = trY;
    
    tst_data = teX;
    tst_y = teY;
    
    data(:,3) = data(:,3)-1;
    data(:,4) = data(:,4)/2;
    
    tst_data(:,3) = tst_data(:,3)-1;
    tst_data(:,4) = tst_data(:,4)/2;
    
end

% suppose all the attributes are ordinal 
N = size(data,1);
D = size(data,2);
K = size(unique(y),1);

%% model parameters. 
W = zeros(D*K,1);
psi = zeros(N,1);
intervals = cell(D,1);

E = 0;   % E is #end points
cumE = zeros(D,1);    % indicates starting position
%% initialization
for i=1:D
    cumE(i) = E;
    % # of ordinal values for each dimension
    n = size(unique(data(:,i)),1);
    E = E + n-1;   
%    interval = linspace(0,1,n+1); % including 0 and 1

    interval = linspace(1/(2*n), (2*n+1)/(2*n), n+1);
    intervals{i}=interval;
end
H = sparse(N*K,1);
G = sparse(N*K,D*K+N);
Q = sparse(D*K+N,1);

% initialize p
P = sparse(D*K+N, D*K+N);
for i=1:D*K
    P(i,i)=1;
end

%% baseline SVM
best_accu = 0;
for regularizer = 4.^[0]
    cmd = ['-q -s 4 -c ', num2str(regularizer), ' -v 5'];
    accu = train(y, sparse(data), cmd);
    if accu > best_accu
        best_reg = regularizer;
        best_accu = accu;
    end
end

regularizer = best_reg;
disp(regularizer);

% norm data
norm_data = data;
norm_tst_data = tst_data;
for i=  1:D
    norm_tst_data(:,i) = norm_tst_data(:,i) / max( norm_data(:,i) );
end


for i=  1:D
    norm_data(:,i) = norm_data(:,i) / max( norm_data(:,i) );
end

cmd = ['-q -s 4 -c ', num2str(regularizer)];
model = train(y, sparse(norm_data), cmd);

[predicted_label, accuracy, decision_values] = predict(y, sparse(norm_data), model);
baseline_accu_train = accuracy(1);

[predicted_label, accuracy, decision_values] = predict(tst_y, sparse(norm_tst_data), model);
baseline_accu = accuracy(1);



% create Q
for j=D*K+1:D*K+N
    Q(j)=regularizer;
end

% baseline knn
%baseline_knn = knncl(eye(D), norm_data', y', norm_tst_data', tst_y', 3);

%% EM-q -step

for itr = 1:8
    disp(['iter = ',num2str(itr), ' ******']);
    %% Fix M, learn W, quadratic programming. 
%     for i=1:N   % for each instance
%         instance = data(1,:)';
%         for k=1:K % for each label
%             if y(i)==k
%                 H((i-1)*K+k)=0;
%             else
%                 H((i-1)*K+k)=-1;
%             end
%        
%             % fill out G
%        
%             % convert to real features
%             realFeatures = zeros(D,1);
%             for j=1:D
%                 inter = intervals{j};
%                 realFeatures(j)=(inter(instance(j))+inter(instance(j)+1))/2;
%             end
%        
%             G((i-1)*K+k,D*K+i)=-1;
%             if y(i)~=k
%                 label = y(i);
%                 % for common version
%                 j=1;
%                 for s=(label-1)*D+1:label*D
%                     G((i-1)*K+k,s) = realFeatures(j)*(-1);
%                     j = j +1;
%                 end
%                 % for current version
%                 j = 1;
%                 for s=(k-1)*D+1:k*D
%                     G((i-1)*K+k,s) = realFeatures(j);
%                     j = j +1;
%                 end 
%             end
%         end
%     end
% 
%     x = quadprog(P,Q,G,H);
%     W = x(1:D*K,1);
%     psi = x(D*K+1:D*K+N);

   %% Fix M, learn W, using Liblinear
   % create a new data matrix
   new_X = zeros(N,D);
   for i = 1:N
       for j = 1:D
           inter = intervals{j};
           new_X(i,j) = (inter( data(i,j) ) + inter( data(i,j)+1 ))/2;
       end
   end
   
   % train libliner
   cmd = ['-q -s 4 -c ', num2str(regularizer)];
   model = train(y, sparse(new_X), cmd);
   
   W = model.w;
   
   % objective value
   psi = zeros(N,1);
   for i = 1:N
       psi_i = 0;
       for c = 1:K
            tmp = (W(c,:) - W(y(i),:))*new_X(i,:)' + (y(i) ~= c);
            psi_i = max( psi_i, tmp);
       end
       psi(i) = psi_i;
   end
   
   obj_val(itr) = regularizer * sum(psi) + sum( sum( W.^2 ) ) / 2;
   
   obj_val_old(1) = 0;
   if itr > 1
       psi = zeros(N,1);
       for i = 1:N
           psi_i = 0;
           for c = 1:K
               tmp = (old_W(c,:) - old_W(y(i),:))*new_X(i,:)' + (y(i) ~= c);
               psi_i = max( psi_i, tmp);
           end
           psi(i) = psi_i;
       end
       
       obj_val_old(itr) = regularizer * sum(psi) + sum( sum( old_W.^2 ) ) / 2;
   end
   
   % generating test data
   tst_N = length(tst_y);
   new_tst_X = zeros(tst_N,D);
   for i = 1:tst_N
       for j = 1:D
           inter = intervals{j};
           new_tst_X(i,j) = (inter( tst_data(i,j) ) + inter( tst_data(i,j)+1 ))/2;
       end
   end
   
   [predicted_label, accuracy, decision_values] = predict(y, sparse(new_X), model);
   ordinal_accu_train(itr) = accuracy(1);
   
   [predicted_label, accuracy, decision_values] = predict(tst_y, sparse(new_tst_X), model);
   ordinal_accu(itr) = accuracy(1);
  
   
%   ordinal_knn{itr} = knncl(eye(D), new_X', y', new_tst_X', tst_y', 3);
   
    sta = tic;
   %% Fix W, learn M, linear programming
    C = zeros(E+N,1);    % objective coefficient
    C(E+1:E+N,1) = 1;
    
    % Ax <= B
    B = sparse(N*K + E + D, 1);
    A = sparse(N*K + E + D, E+N);
    
    for i=1:N % for each instance
        instance = data(i,:);
        
        for k =1:K   % for each k
            row_idx = (i-1)*K+k;
            
            % fill out B
            if y(i)==k
                B(row_idx)=0;
            else
                B(row_idx)=-1;
            end
            
            % fill out the row of A
            A(row_idx, E+i) = -1;
            if y(i)~= k
                label = y(i);
                a = W(k,:)-W(label,:);   % w_k - w_label
                
                for j = 1:D   % iterate through all the attributes
                    delta = 0.5*a(j);
                    interval = intervals{j};
                    
                    if instance(j)==1 % only include the first end point
                        A(row_idx, cumE(j)+1) = delta;
                        B(row_idx) = B(row_idx) - delta * interval(1);
                        
                    elseif instance(j)== length(interval)-1 % only include the last end point
                        A(row_idx, cumE(j)+length(interval)-2) = delta;
                       % A(row_idx,E+i) = A((i-1)*K+k,E+i) - 1/2 * a(j);
                        B(row_idx) = B(row_idx) - delta * interval(end);
     
                    else  % include both of end points
                        A(row_idx, cumE(j)+instance(j)-1) = delta;
                        A(row_idx, cumE(j)+instance(j)) = delta;
                    end
                end
            end
        end
    end
    
    cumE2 = cumE;
    for i = 2:D
        cumE2(i) = cumE2(i)+i-1;
    end
    for i= 1:D   % iterate through constraints 
        interval = intervals{i};
        for j = 1:length(interval)-1
            row_idx = N*K+cumE2(i)+j;
            if j==1
                A(row_idx,cumE(i)+j) = -1;
                B(row_idx) = -1/(2*(length(interval)-1));   % new
            elseif j == length(interval)-1
                A(row_idx, cumE(i)+j-1) = 1;
            %    B(row_idx) = 1;
                B(row_idx) = 1 + 1/(2*(length(interval)-1));
            else
                A(row_idx,cumE(i)+j-1) = 1;
                A(row_idx,cumE(i)+j) = -1;
                B(row_idx) = 0.1;
            end
        end
    end
    
%     init_Var = zeros(E+N,1);
%     cc = 0;
%     for i = 1:D
%         interval = intervals{i};
%         init_Var(cc+1:cc+length(interval)-2) = interval(2:end-1)';
%         cc = cc + length(interval)-2;
%     end
%     init_Var = [init_Var;psi];
    dur = toc(sta);
    disp('generating matrix');
    disp(dur);
    
    sta = tic;
    disp('linear programming');
    Var = linprog(C,A,B);
    dur = toc(sta);
    disp(dur);
    
    % objective value
    psi = Var(E+1:end);
    
    obj_val2(itr) = regularizer * sum(psi) + sum( sum( W.^2 ) ) / 2;
    
    old_W = W;
    
    cc = 0;
    for i = 1:D
        mm = intervals{i};
        for j = 2:length(mm)-1
            cc = cc + 1;
            mm(j) = Var(cc);
        end
        intervals{i} = mm;
    end
    
    for v = 1:itr
    fprintf('*** %g obj: %g\t%g\n', v, obj_val(v), obj_val2(v));
    end
    keyboard    
end

keyboard
