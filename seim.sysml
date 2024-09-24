library package SEIM {
    action def Function;
    allocation def AllocatedFunction {
        end function : Function;
        end component : LogicalConstituent;
    }

    interface def FunctionalFlow {
        end source;
        end target;
    }

    interface def LogicalFlow {
        end source;
        end target;
    }

    part def LogicalConstituent;

    port def FunctionPort;
    port def ConstituentPort;
}
